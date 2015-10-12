package com.carrey.e_bus_home;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PullToRefreshView extends LinearLayout {
    private static final String TAG = "PullToRefreshView";
    // refresh states
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;
    // pull state
    private static final int PULL_UP_STATE = 0;
    private static final int PULL_DOWN_STATE = 1;
    // for mScroller, scroll back from header or footer.
    private final static int SCROLL_DURATION = 300; // scroll back duration
    private Scroller mScroller; // used for scroll back

    private OnPullBaseScrollChanged onPullBaseScrollChanged;
    /**
     * last y
     */
    private int mLastMotionY;
    /**
     * lock
     */
    private boolean mLock;
    /**
     * header view
     */
    private View mHeaderView;
    /**
     * footer view
     */
    private View mFooterView;
    /**
     * list or grid
     */
    private AdapterView<?> mAdapterView;
    /**
     * scrollview
     */
    private ScrollView mScrollView;
    /**
     * recycleview
     */
    private RecyclerView mRecyclerView;
    /**
     * header view height
     */
    private int mHeaderViewHeight;
    /**
     * footer view height
     */
    private int mFooterViewHeight;
    /**
     * header view image
     */
    private ImageView mHeaderImageView;
    /**
     * footer view image
     */
    private ImageView mFooterImageView;
    /**
     * header tip text
     */
    private TextView mHeaderTextView;
    /**
     * footer tip text
     */
    private TextView mFooterTextView;
    /**
     * header refresh time
     */
    private TextView mHeaderUpdateTextView;
    /**
     * footer progress bar
     */
    private ProgressBar mFooterProgressBar;
    /**
     * layout inflater
     */
    private LayoutInflater mInflater;
    /**
     * header view current state
     */
    private int mHeaderState;
    /**
     * footer view current state
     */
    private int mFooterState;
    /**
     * pull state,pull up or pull down;PULL_UP_STATE or PULL_DOWN_STATE
     */
    private int mPullState;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    /**
     * footer refresh listener
     */
    private OnFooterRefreshListener mOnFooterRefreshListener;
    /**
     * footer refresh listener
     */
    private OnHeaderRefreshListener mOnHeaderRefreshListener;
    /**
     * last update time
     */
    // private String mLastUpdateTime;

    private int delata = 5;

    /**
     * 是否允许加载更多
     */
    private boolean mLoadMoreEnable = true;

    /**
     * 是否允许下拉刷新
     */
    private boolean mRefreshEnable = true;

    private AnimationDrawable mHeaderDrawable;
    //    private CountDownTimer mTimer;
    private boolean mRefreshing;

    private MotionEvent mLastMoveEvent;

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshView(Context context) {
        super(context);
        init();
    }

    private void init() {
        delata = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mInflater = LayoutInflater.from(getContext());
        addHeaderView();
    }

    @SuppressWarnings("deprecation")
    private void addHeaderView() {
        // header view
        mHeaderView = mInflater.inflate(R.layout.refresh_header, this, false);

        mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_refresh_image);
        mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_text);
        mHeaderUpdateTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
//        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_refresh_progress);
        // header layout
        measureView(mHeaderView);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, mHeaderViewHeight);
        params.topMargin = -(mHeaderViewHeight);
        addView(mHeaderView, params);

        mHeaderUpdateTextView.setText(getTransforDate(System.currentTimeMillis()));

        mHeaderDrawable = (AnimationDrawable) mHeaderImageView.getDrawable();
//        mHeaderDrawable = (LevelListDrawable) drawable.findDrawableByLayerId(R.id.front);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onPullBaseScrollChanged != null) {
            onPullBaseScrollChanged.onPullBaseScrollChanged(l, t, oldl, oldt);
        }
    }

    @SuppressWarnings("deprecation")
    private void addFooterView() {
        // footer view
        mFooterView = mInflater.inflate(R.layout.refresh_footer, this, false);
        mFooterImageView = (ImageView) mFooterView.findViewById(R.id.pull_to_load_image);
        mFooterTextView = (TextView) mFooterView.findViewById(R.id.pull_to_load_text);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pull_to_load_progress);
        // footer layout
        measureView(mFooterView);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, mFooterViewHeight);
        addView(mFooterView, params);
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        mLoadMoreEnable = loadMoreEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        mRefreshEnable = refreshEnable;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addFooterView();
        initContentAdapterView();
    }

    private void initContentAdapterView() {
        int count = getChildCount();
        if (count < 3) {
            throw new IllegalArgumentException("this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
        }
        View view = null;
        for (int i = 0; i < count - 1; ++i) {
            view = getChildAt(i);
            if (view instanceof AdapterView<?>) {
                mAdapterView = (AdapterView<?>) view;
                mAdapterView.setOverScrollMode(OVER_SCROLL_NEVER);
            }
            if (view instanceof ScrollView) {
                // finish later
                mScrollView = (ScrollView) view;
                mScrollView.setOverScrollMode(OVER_SCROLL_NEVER);
            }
            if (view instanceof RecyclerView) {
                mRecyclerView = (RecyclerView) view;
                mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    private int dy;

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE && dy > delata) {
                            if (isRefreshViewScroll(-delata - 1)) {
                                mScroller.startScroll(0, getScrollY(), 0, mFooterViewHeight, 200);
                                invalidate();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        footerRefreshing();
                                    }
                                }, 200);
                            }
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        this.dy = dy;
                    }
                });
            }
        }

        if (mAdapterView == null && mScrollView == null && mRecyclerView == null) {
            throw new IllegalArgumentException("must contain a AdapterView or ScrollView in this layout!");
        }
    }

    @SuppressWarnings("deprecation")
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                // deltaY > 0 下拉
                int deltaY = y - mLastMotionY;
                if (isRefreshViewScroll(deltaY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mAdapterView != null) {
                    mAdapterView.clearFocus();// add by chenbo
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLock) {
            return true;
        }
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastMotionY;
                if (mRefreshEnable && mPullState == PULL_DOWN_STATE) {
//                    Log.i(TAG, " pull down!parent view move!");
                    if (getScrollY() < 0 || deltaY > 0) {
                        headerPrepareToRefresh(deltaY);
                    } else {
                        sendDownEvent();
                    }
                    // setHeaderPadding(-mHeaderViewHeight);
                } else if (mLoadMoreEnable && mPullState == PULL_UP_STATE) {
//                    Log.i(TAG, "pull up!parent view move!");
                    if (getScrollY() > 0 || deltaY < 0) {
                        footerPrepareToRefresh(deltaY);
                    } else {
                        sendDownEvent();
                    }
                }
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int topMargin = getHeaderTopMargin();
                if (mRefreshEnable && mPullState == PULL_DOWN_STATE) {
                    if (Math.abs(topMargin) >= mHeaderViewHeight) {
                        mRefreshing = true;
                        headerRefreshing();
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_DURATION);
                        invalidate();
                    }
                } else if (mLoadMoreEnable && mPullState == PULL_UP_STATE) {
                    if (Math.abs(topMargin) >= mFooterViewHeight) {
                        footerRefreshing();
                    } else {
//                        setHeaderTopMargin(-mHeaderViewHeight);
                        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_DURATION);
                        invalidate();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void sendDownEvent() {
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEvent(e);
    }

    private boolean isRefreshViewScroll(int deltaY) {
        if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
            return true;
        }
        if (mAdapterView != null) {
            if (deltaY > delata) {

                View child = mAdapterView.getChildAt(0);
                if (child == null) {
                    return false;
                }
                if (mAdapterView.getFirstVisiblePosition() == 0 && child.getTop() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
                int top = child.getTop();
                int padding = mAdapterView.getPaddingTop();
                if (mAdapterView.getFirstVisiblePosition() == 0 && Math.abs(top - padding) <= 8) {// 
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }

            } else if (deltaY < -delata) {
                if (!mLoadMoreEnable) {
                    return false;
                }
                View lastChild = mAdapterView.getChildAt(mAdapterView.getChildCount() - 1);
                if (lastChild == null) {
                    return false;
                }
                if (lastChild.getBottom() <= getHeight() && mAdapterView.getLastVisiblePosition() == mAdapterView.getCount() - 1) {
                    mPullState = PULL_UP_STATE;
                    return true;
                }
            }
        }
        if (mScrollView != null) {
            View child = mScrollView.getChildAt(0);
            if (deltaY > 0 && mScrollView.getScrollY() == 0) {
                mPullState = PULL_DOWN_STATE;
                return true;
            } else if (deltaY < 0 && child.getMeasuredHeight() <= getHeight() + mScrollView.getScrollY()) {
                if (!mLoadMoreEnable) {
                    return false;
                }
                mPullState = PULL_UP_STATE;
                return true;
            }
        }
        if (mRecyclerView != null) {
            if (deltaY > delata) {

                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    return false;
                }
                int firstPosition = 0;
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    firstPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof GridLayoutManager) {
                    firstPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    firstPosition = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null)[0];
                }
                if (firstPosition == 0 && child.getTop() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
                int top = child.getTop();
                int padding = mRecyclerView.getPaddingTop();
                if (firstPosition == 0 && Math.abs(top - padding) <= 8) {//
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }

            } else if (deltaY < -delata) {
                if (!mLoadMoreEnable) {
                    return false;
                }
                View lastChild = mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1);
                if (lastChild == null) {
                    return false;
                }
                int lastPosition = 0;
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else if (layoutManager instanceof GridLayoutManager) {
                    lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    lastPosition = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null)[0];
                }
                if (lastChild.getBottom() <= getHeight() && lastPosition == mRecyclerView.getLayoutManager().getItemCount() - 1) {
                    mPullState = PULL_UP_STATE;
                    return true;
                }
            }
        }
        return false;
    }

    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);

        // 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
        if (Math.abs(newTopMargin) >= mHeaderViewHeight && mHeaderState != RELEASE_TO_REFRESH) {
//            mHeaderDrawable.setLevel(7);
//            ((AnimationDrawable) mHeaderImageView.getDrawable()).selectDrawable(2);
            mHeaderDrawable.start();
            mHeaderTextView.setText(R.string.pull_to_refresh_release_label);
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderState = RELEASE_TO_REFRESH;
        } else if (/* && */Math.abs(newTopMargin) < mHeaderViewHeight) {// 拖动时没有释放
//            mHeaderDrawable.setLevel(0);
//            if (Math.abs(newTopMargin) > mHeaderViewHeight / 2) {
//            }
//            ((AnimationDrawable) mHeaderImageView.getDrawable()).selectDrawable(6);
            if (mHeaderState != PULL_TO_REFRESH) {
                mHeaderDrawable.stop();
                mHeaderTextView.setText(R.string.pull_to_refresh_pull_label);
//                ((AnimationDrawable) mHeaderImageView.getDrawable()).start();
            }
            mHeaderDrawable.selectDrawable(7 * (mHeaderViewHeight - Math.abs(newTopMargin)) / mHeaderViewHeight);
            mHeaderState = PULL_TO_REFRESH;
        }
    }

    private void footerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        if (Math.abs(newTopMargin) >= (mFooterViewHeight) && mFooterState != RELEASE_TO_REFRESH) {
            mFooterTextView.setText(R.string.pull_to_refresh_footer_release_label);
            mFooterImageView.clearAnimation();
            mFooterImageView.startAnimation(mFlipAnimation);
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(newTopMargin) < (mFooterViewHeight) && mFooterState != PULL_TO_REFRESH) {
            mFooterImageView.clearAnimation();
            mFooterImageView.startAnimation(mReverseFlipAnimation);
            mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
            mFooterState = PULL_TO_REFRESH;
        }
    }

    private int changingHeaderViewTopMargin(int deltaY) {
        scrollBy(0, -(int) (deltaY * 0.4f));
        return getScrollY();
    }

    private void headerRefreshing() {
        mHeaderState = REFRESHING;
        mScroller.startScroll(0, getScrollY(), 0, Math.abs(getScrollY()) - mHeaderViewHeight, SCROLL_DURATION);
        invalidate();
//        startHeaderAnim();
//        ((AnimationDrawable)mHeaderImageView.getDrawable()).start();
        mHeaderTextView.setText(R.string.pull_to_refresh_refreshing_label);
        if (mOnHeaderRefreshListener != null) {
            mOnHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    private void footerRefreshing() {
        mFooterState = REFRESHING;
        mScroller.startScroll(0, getScrollY(), 0, mFooterViewHeight - Math.abs(getScrollY()), SCROLL_DURATION);
        invalidate();
        mFooterImageView.setVisibility(View.GONE);
        mFooterImageView.clearAnimation();
        mFooterImageView.setImageDrawable(null);
        mFooterProgressBar.setVisibility(View.VISIBLE);
        mFooterTextView.setText(R.string.pull_to_refresh_footer_refreshing_label);
        if (mOnFooterRefreshListener != null) {
            mOnFooterRefreshListener.onFooterRefresh(this);
        }
    }


    public void onHeaderRefreshComplete() {
        mHeaderUpdateTextView.setText(getTransforDate(System.currentTimeMillis()));
        resetHeaderHeight();
//        stopHeaderAnim();
//        mTimer = null;
        mRefreshing = false;
//        ((AnimationDrawable)mHeaderImageView.getDrawable()).stop();
        mHeaderDrawable.stop();
        mHeaderState = PULL_TO_REFRESH;
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        if (mHeaderState == REFRESHING) {
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_DURATION);
            // trigger computeScroll
            invalidate();
        }
    }

    private void resetFooterHeight() {
        if (mFooterState == REFRESHING) {
            if (mRecyclerView != null) {
                if (getScrollY() > mFooterViewHeight) {
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + mFooterViewHeight, SCROLL_DURATION);
                    invalidate();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 0);
                            invalidate();
                            mRecyclerView.scrollBy(0, mFooterViewHeight);
                        }
                    }, SCROLL_DURATION);
                } else {
                    mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 0);
                    invalidate();
                    mRecyclerView.scrollBy(0, mFooterViewHeight);
                }
            } else {
                mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_DURATION);
                invalidate();
            }

        }
    }

    /**
     * Resets the list to a normal state after a refresh.
     *
     * @param lastUpdated Last updated at.
     */
    public void onHeaderRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onHeaderRefreshComplete();
    }

    public void onFooterRefreshComplete() {
        resetFooterHeight();
        mFooterImageView.setVisibility(View.VISIBLE);
        mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
        mFooterProgressBar.setVisibility(View.GONE);
        mHeaderUpdateTextView.setText(getTransforDate(System.currentTimeMillis()));
        mFooterState = PULL_TO_REFRESH;
    }

    private SimpleDateFormat mSimpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    ;

    private String getTransforDate(long time) {
        return mSimpleFormat.format(new Date(time));
    }


    /**
     * Set a text to represent when the list was last updated.
     *
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderUpdateTextView.setText(lastUpdated);
        } else {
            mHeaderUpdateTextView.setVisibility(View.GONE);
        }
    }

    private int getHeaderTopMargin() {
        return getScrollY();
    }

    @SuppressWarnings("unused")
    private void lock() {
        mLock = true;
    }

    @SuppressWarnings("unused")
    private void unlock() {
        mLock = false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

//    private void startHeaderAnim() {
//        mTimer = new CountDownTimer(Integer.MAX_VALUE, 50) {
//            @Override
//            public void onTick(long l) {
//                int level = mHeaderDrawable.getLevel();
//                if (level == 7) {
//                    mHeaderDrawable.setLevel(0);
//                } else {
//                    mHeaderDrawable.setLevel(++level);
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                mHeaderDrawable.setLevel(7);
//            }
//
//        }.start();
//    }

//    private void stopHeaderAnim() {
//        if (mTimer != null) {
//            mTimer.cancel();
//            mHeaderDrawable.setLevel(7);
//        }
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (mTimer != null) {
//            stopHeaderAnim();
//            startHeaderAnim();
//        }
//    }

//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        stopHeaderAnim();
//    }

    /**
     * set headerRefreshListener
     *
     * @param headerRefreshListener
     * @description
     */
    public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
        mOnHeaderRefreshListener = headerRefreshListener;
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener) {
        mOnFooterRefreshListener = footerRefreshListener;
    }

    /**
     * Interface definition for a callback to be invoked when list/grid footer view should be
     * refreshed.
     */
    public interface OnFooterRefreshListener {
        public void onFooterRefresh(PullToRefreshView view);
    }

    /**
     * Interface definition for a callback to be invoked when list/grid header view should be
     * refreshed.
     */
    public interface OnHeaderRefreshListener {
        public void onHeaderRefresh(PullToRefreshView view);
    }

    public static interface OnPullBaseScrollChanged {
        public void onPullBaseScrollChanged(int l, int t, int oldl, int oldt);
    }

    public final void setOnPullBaseScrollChangedListener(OnPullBaseScrollChanged listener) {
        onPullBaseScrollChanged = listener;
    }
}
