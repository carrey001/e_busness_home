package com.carrey.e_bus_home;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.carrey.e_bus_home.anim.FadingHeadViewHelper;
import com.carrey.e_bus_home.anim.HomeSearchBarAnimation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PullToRefreshView.OnHeaderRefreshListener {

    private PullToRefreshView mPull_view;
    private BaseRecyclerView mRecycler_view;
    private FrameLayout mFl_homeTitle;
    private ViewPagerWithIndicator mHome_vp;

    private HomeSearchBarAnimation animationForSearchBar;
    private FadingHeadViewHelper mFadingHelper;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        List<Integer> listad = new ArrayList();
        listad.add(R.mipmap.ic_launcher);
        listad.add(R.mipmap.ic_launcher1);
        listad.add(R.mipmap.ic_launcher2);
        mHome_vp.getLayoutParams().height = SystemUtil.getScreenWidth() / 2;
        mHome_vp.stopAutoScroll();
        mHome_vp.initViewPager(new BannerAdapter(listad));
        mHome_vp.startAutoScroll();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i + "");
        }
        mRecycler_view.setAdapter(new ArticleAdapter(this, list));

    }

    private void initView() {
        mPull_view = (PullToRefreshView) findViewById(R.id.pull_view);
        mRecycler_view = (BaseRecyclerView) findViewById(R.id.recycler_view);
        mPull_view.setLoadMoreEnable(false);
        mPull_view.setOnHeaderRefreshListener(this);
        View headView = LayoutInflater.from(this).inflate(R.layout.layout_home_head, null);
        mRecycler_view.addHeader(headView);

        mHome_vp = (ViewPagerWithIndicator) headView.findViewById(R.id.home_vp);

        initTitleAnimation();
        mPull_view.setOnPullBaseScrollChangedListener(new PullToRefreshView.OnPullBaseScrollChanged() {
            @Override
            public void onPullBaseScrollChanged(int l, int t, int oldl, int oldt) {
                if (t >= 0) {
                    showTitleBar();
                } else {
                    animationForSearchBar.doAnimateHide();
                }
            }
        });
    }

    /**
     * 首页通知handler
     */
    Handler mMessagehandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
//                loadScheduleData();
            }
            super.handleMessage(msg);
        }
    };

    private void showTitleBar() {
        mMessagehandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animationForSearchBar.doAnimateShow();
            }
        }, 500);
    }

    private void initTitleAnimation() {
        mFl_homeTitle = (FrameLayout) findViewById(R.id.home_title_root_id);
        mFadingHelper = new FadingHeadViewHelper(findViewById(R.id.home_title_root_id), getResources().getDrawable(R.color.bc4));
        animationForSearchBar = new HomeSearchBarAnimation(this, mFl_homeTitle);
        mRecycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int deltaY;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                deltaY += dy;
                animationForSearchBar.scrollHeaderTo(-deltaY);
            }
        });
        if (SystemUtil.isTintStatusBarAvailable(this)) {
            mFl_homeTitle.setPadding(0, SystemUtil.getStatusBarHeight(), 0, 0);
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        handler.sendEmptyMessageDelayed(1, 1000);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPull_view.onHeaderRefreshComplete();
        }
    };

    public FrameLayout getHomeTitle() {
        return mFl_homeTitle;
    }

    public FadingHeadViewHelper getFadingHelper() {
        return mFadingHelper;
    }
    /**
     * 广告adapter
     */
    public class BannerAdapter extends PagerAdapter {
        private final List<Integer> mBanner;
        private final int mCount;

        public BannerAdapter(List<Integer> mBanner) {
            this.mBanner = mBanner;
            mCount = mBanner.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final int pos = position % mCount;

            ImageView imageView = new ImageView(MainActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(params);
            imageView.setImageResource(mBanner.get(pos));
//            mBitmapTools.display(imageView, mBanner.get(pos).img);

//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (mBanner.get(pos) != null) {
//                        SchemeUtil.startActivity(mBaseActivity, mBanner.get(pos).url);
//                    }
//
//                    Map map = new HashMap();
//                    map.put("Control_name", "adv" + (pos + 1));
//                    UmengClickAgent.onEvent(mBaseActivity, "JkyHomeAdvertisements", map);
//                }
//            });

            container.addView(imageView, params);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }
}
