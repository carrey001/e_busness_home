package com.carrey.e_bus_home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * 自定义带圆点的viewpager
 * tanghaihua
 * 2015年6月29日
 */
public class ViewPagerWithIndicator extends RelativeLayout {

    private Context mContext;
    private CustomIndicator mCustomIndicator;
    private AutoScrollViewPager mViewPager;
    private OnPageSelectListener onPageSelectListener;
    private int mCount;

    public ViewPagerWithIndicator(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }
	
    public ViewPagerWithIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }
    
    private void init(AttributeSet attrs) {
        mViewPager = new AutoScrollViewPager(mContext);
        
        if(attrs != null){
            mCustomIndicator = new CustomIndicator(mContext, attrs);
        }else{
            mCustomIndicator = new CustomIndicator(mContext);
        }
        
        LayoutParams layoutParams1 = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        
        LayoutParams layoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mCustomIndicator.setPadding(0, 0, 0, UIUtil.dip2px(8));
        
        this.addView(mViewPager, layoutParams1);
        this.addView(mCustomIndicator, layoutParams2);
        
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                mCustomIndicator.setCurrentPosition(pos % mCount);
                if(onPageSelectListener != null){
                    onPageSelectListener.onPageSelected(pos);
                }
            }
            
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    
    /**
     * 添加页面选中监听
     * setOnPageSelectListener
     * @param onPageSelectListener
     * @since 1.0
     */
    public void setOnPageSelectListener(OnPageSelectListener onPageSelectListener){
        this.onPageSelectListener = onPageSelectListener;
    }
    
    public interface OnPageSelectListener{
        public void onPageSelected(int pos);
    }

    /**
     * 初始化viewpager
     * initViewPager
     * @param pagerAdapter
     * @since 1.0
     */
    public void initViewPager(PagerAdapter pagerAdapter) {
        if(pagerAdapter != null){
            mCount = pagerAdapter.getCount();
            if(mCount > 1){
                mCustomIndicator.setCount(mCount);
            }
            mViewPager.setAdapter(pagerAdapter);
//            int maxSize = 65535;
//            int pos = maxSize / 2 - maxSize / 2 % mCount; // 计算初始位置
//            mViewPager.setCurrentItem(pos);
        }
    }
    
    /**
     * 自动轮播
     */
    public void startAutoScroll() {
        if(mViewPager != null){
            mViewPager.startAutoScroll();
        }
    }
    
    /**
     * 自动轮播
     * @param startTime 过几秒轮播
     * @param interval 图片轮播间隔时间
     */
    public void startAutoScroll(int startTime, int interval) {
        if(mViewPager != null){
            mViewPager.setInterval(interval);
            mViewPager.startAutoScroll(startTime);
        }
    }
    
    /**
     * 停止轮播
     */
    public void stopAutoScroll() {
        if(mViewPager != null){
            mViewPager.stopAutoScroll();
        }
    }
    
//    /**
//     * 初始化viewpager
//     * initViewPager
//     * @param mBitmapTools
//     * @param imageUrl
//     * @since 1.0
//     */
//    public void initViewPager(final BitmapTools mBitmapTools, final List<String> imageUrl) {
//        if(imageUrl != null && imageUrl.size() > 0){
//            mCount = imageUrl.size();
//            if(mCount > 1){
//                mCustomIndicator.setCount(mCount);
//            }
////            mCustomIndicator.setCount(mCount);
//
//            PagerAdapter adapter = new PagerAdapter() {
//                @Override
//                public void destroyItem(ViewGroup container, int position,
//                        Object object) {
//                    container.removeView((View) object);
//                }
//
//                @Override
//                public Object instantiateItem(ViewGroup container, int position) {
//                    int pos = position % mCount;
//
//                    ImageView imageView = new ImageView(mContext);
//                    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
//                            LayoutParams.MATCH_PARENT);
//                    imageView.setLayoutParams(params);
//                    mBitmapTools.display(imageView, imageUrl.get(pos));
//
//                    container.addView(imageView, params);
//                    return imageView;
//                }
//
//                @Override
//                public boolean isViewFromObject(View view, Object object) {
//                    return view == object;
//                }
//
//                @Override
//                public int getCount() {
//                    if(mCount == 1){
//                        return 1;
//                    }
//                    return Integer.MAX_VALUE;
//                }
//            };
//
//            mViewPager.setAdapter(adapter);
//            int maxSize = 65535;
//            int pos = maxSize / 2 - maxSize / 2 % mCount; // 计算初始位置
//            mViewPager.setCurrentItem(pos);
//        }
//    }
    
    /**
     * 初始化数据
     * initViewPager
     *
     * @param views (页面所有view集合)
     * @since 1.0
     */
    public void initViewPager(final List<View> views) {
        if(views == null){
            return;
        }
        
        mCount = views.size();
        if(mCount > 1){
            mCustomIndicator.setCount(mCount);
        }

        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public void destroyItem(ViewGroup container, int position,
                    Object object) {
//                int pos = position % mCount;
//                View view = views.get(pos);
//                container.removeView(view);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
//                int pos = position % mCount;
//                View view = views.get(pos);
//                container.addView(view);
//                return view;

                //对ViewPager页号求模取出View列表中要显示的项
                int pos = position % mCount;
                if (pos < 0){
                    pos = mCount + pos;
                }
                View view = views.get(pos);
                //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
                ViewParent vp =view.getParent();
                if (vp!=null){
                    ViewGroup parent = (ViewGroup)vp;
                    parent.removeView(view);
                }
                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
//                int pos = (Integer)object % mCount;
//                boolean f = view == views.get(pos);
//                return f;

                return view == object;
            }
            
            @Override
            public int getCount() {
                if(mCount == 1){
                    return 1;
                }
                return Integer.MAX_VALUE;
            }
        };
        
        mViewPager.setAdapter(adapter);
        int maxSize = 65535;
        int pos = maxSize / 2 - maxSize / 2 % mCount; // 计算初始位置
        mViewPager.setCurrentItem(pos);
    }
}
