package com.carrey.e_bus_home;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

/**
 * Created by chenbo on 2015/6/11.
 */
public class BaseRecyclerView extends RecyclerView {
    public static final int LIST = 0;
    public static final int GRID = 1;
    public static final int STAGGERED_GRID = 2;

    private View mHeader;
    private View mFooter;

    private ProxyAdapter mProxyAdapter;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private int mItemSelectorId;
    private int mDividerId;
    private int mDividerHeight;
    private int mStyle;
    private int mNumColumns;
    private int mOrientation;
    private int mVerticalSpacing;
    private int mHorizontalSpacing;

    public BaseRecyclerView(Context context) {
        this(context, null);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseRecyclerView);
            mItemSelectorId = a.getResourceId(R.styleable.BaseRecyclerView_android_listSelector, 0);
            mDividerId = a.getResourceId(R.styleable.BaseRecyclerView_android_divider, 0);
            mDividerHeight = a.getDimensionPixelSize(R.styleable.BaseRecyclerView_android_dividerHeight, 1);
            mStyle = a.getInt(R.styleable.BaseRecyclerView_recyclerStyle, LIST);
            mNumColumns = a.getInt(R.styleable.BaseRecyclerView_android_numColumns, 1);
            mOrientation = a.getInt(R.styleable.BaseRecyclerView_android_orientation, RecyclerView.VERTICAL);
            mVerticalSpacing = a.getDimensionPixelSize(R.styleable.BaseRecyclerView_android_verticalSpacing, 0);
            mHorizontalSpacing = a.getDimensionPixelSize(R.styleable.BaseRecyclerView_android_horizontalSpacing, 0);
            a.recycle();

//            LogUtils.d("bacy" + mOrientation);
        }

        switch (mStyle) {
            case LIST:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(mOrientation);
                setLayoutManager(linearLayoutManager);
                if (mOrientation == HORIZONTAL) {
                    addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST, mDividerId, mDividerHeight));
                } else {
                    addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST, mDividerId, mDividerHeight));
                }
                break;
            case GRID:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, mNumColumns);
                gridLayoutManager.setOrientation(mOrientation);
                setLayoutManager(gridLayoutManager);
                break;
            case STAGGERED_GRID:
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(mNumColumns, mOrientation);
                setLayoutManager(staggeredGridLayoutManager);
                break;
            default:
                break;
        }

        setItemAnimator(null);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mProxyAdapter = new ProxyAdapter(this, adapter);
        mProxyAdapter.setOnItemClickListener(mOnItemClickListener);
        mProxyAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
        mProxyAdapter.addHeader(mHeader);
        mProxyAdapter.addFooter(mFooter);
        mProxyAdapter.setItemSelectorId(mItemSelectorId);
        mProxyAdapter.setSpace(mVerticalSpacing, mHorizontalSpacing);
        if (mStyle == GRID) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager)getLayoutManager();
            gridLayoutManager.setSpanSizeLookup(
                    mProxyAdapter.createSpanSizeLookup(mNumColumns));
        }
        super.setAdapter(mProxyAdapter);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
        if (mProxyAdapter != null) {
            mProxyAdapter.setOnItemClickListener(mOnItemClickListener);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        mOnItemLongClickListener = itemLongClickListener;
        if (mProxyAdapter != null) {
            mProxyAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
        }
    }

    public void addHeader(View header) {
        mHeader = header;
        if (mProxyAdapter != null) {
            mProxyAdapter.addHeader(header);
        }
    }

    public void addFooter(View footer) {
        mFooter = footer;
        if (mProxyAdapter != null) {
            mProxyAdapter.addFooter(footer);
        }
    }

    public void setSpace(int verticalSpacing, int horizontalSpacing) {
        mVerticalSpacing = verticalSpacing;
        mHorizontalSpacing = horizontalSpacing;
    }

    public static class ProxyAdapter<VH extends ViewHolder> extends Adapter<ViewHolder> {
        protected static final int TYPE_HEADER = 111;
        protected static final int TYPE_FOOTER = 112;

        private View header;
        private View footer;

        private RecyclerView recyclerView;
        private Adapter adapter;

        private int itemSelectorId;
        private int verticalSpacing;
        private int horizontalSpacing;

        private OnItemClickListener onItemClickListener;
        private OnItemLongClickListener onItemLongClickListener;

        public ProxyAdapter(RecyclerView parent, Adapter adapter) {
            this.adapter = adapter;
            this.recyclerView = parent;

            adapter.registerAdapterDataObserver(new AdapterDataObserver() {
                @Override
                public void onChanged() {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
                }

                public void onItemRangeInserted(int positionStart, int itemCount) {
                    notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
                }

                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
                }

                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    notifyItemRangeRemoved(fromPosition + getHeaderCount(), (toPosition - fromPosition - getHeaderCount()));
                }
            });
        }

        @Override
        public int getItemViewType(int position) {

            if (isHeaderPosition(position)) {
                return TYPE_HEADER;
            }
            if (isFooterPosition(position)) {
                return TYPE_FOOTER;
            }
            return adapter.getItemViewType(position - getHeaderCount());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_HEADER:
                    return new AdvanceHolder(getHeader());

                case TYPE_FOOTER:
                    return new AdvanceHolder(getFooter());

                default:
                    RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(parent, viewType);
                    if (verticalSpacing != 0 || horizontalSpacing != 0) {
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)holder.itemView.getLayoutParams();
                        if (params == null) {
                            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            holder.itemView.setLayoutParams(params);
                        }
                        params.topMargin = params.bottomMargin = verticalSpacing;
                        params.rightMargin = params.leftMargin = horizontalSpacing;
                    }
                    return holder;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            final int viewType = getItemViewType(position);
            final int realPosition = getRealPosition(position);
            switch (viewType) {
                case TYPE_HEADER:
                case TYPE_FOOTER:
                    if (viewHolder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                        StaggeredGridLayoutManager.LayoutParams layoutParams =
                                (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                        layoutParams.setFullSpan(true);
                    }
                    break;

                default:
                    adapter.onBindViewHolder(viewHolder, realPosition);
                    break;
            }

            //  注册点击事件
            if (onItemClickListener != null) {

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isFullSpan(position)) {
                            onItemClickListener.onItemClick(recyclerView, view, realPosition, getItemId(realPosition));
                        }
                    }
                });
            }
            if (onItemLongClickListener != null) {

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!isFullSpan(position)) {
                            return onItemLongClickListener.onItemLongClick(recyclerView, view, realPosition, getItemId(realPosition));
                        }

                        return false;
                    }
                });
            }

            // 添加item背景
            if (itemSelectorId != 0) {
                if (!isFullSpan(position)) {
                    viewHolder.itemView.setBackgroundResource(itemSelectorId);
                }
            }
        }

        @Override
        public void setHasStableIds(boolean hasStableIds) {
            adapter.setHasStableIds(hasStableIds);
        }

        @Override
        public long getItemId(int position) {
            if (isHeaderPosition(position) || isFooterPosition(position)) {
                return super.getItemId(position);
            }
            return adapter.getItemId(getRealPosition(position));
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            if (holder instanceof AdvanceHolder) {
                super.onViewAttachedToWindow(holder);
            } else {
                adapter.onViewAttachedToWindow(holder);
            }
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            if (holder instanceof AdvanceHolder) {
                super.onViewDetachedFromWindow(holder);
            } else {
                adapter.onViewDetachedFromWindow(holder);
            }
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            if (holder instanceof AdvanceHolder) {
                return super.onFailedToRecycleView(holder);
            }
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            if (holder instanceof AdvanceHolder) {
                super.onViewRecycled(holder);
            } else {
                adapter.onViewRecycled(holder);
            }
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            this.onItemLongClickListener = onItemLongClickListener;
        }

        public void setItemSelectorId(int itemSelectorId) {
            this.itemSelectorId = itemSelectorId;
        }

        public void setSpace(int verticalSpacing, int horizontalSpacing) {
            this.verticalSpacing = verticalSpacing;
            this.horizontalSpacing = horizontalSpacing;
        }

        public boolean isFullSpan(int position) {
            return isHeaderPosition(position) || isFooterPosition(position);
        }

        public void addHeader(View v) {
            if (v != null)  {
                setDefaultLayoutParams(v);
            }
            header = v;
        }

        public void addFooter(View v) {
            if (v != null) {
                setDefaultLayoutParams(v);
            }
            footer = v;
        }

        private void setDefaultLayoutParams(View v) {
            if (v.getLayoutParams() == null) {
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(lp);
            }
        }

        private boolean isFooterPosition(int position) {
            int footerCount = getFooterCount();

            if (footerCount == 0) {
                return false;
            } else {
                return position == (getHeaderCount() + adapter.getItemCount());
            }

        }

        private boolean isHeaderPosition(int position) {
            return position < getHeaderCount();
        }

        private int getRealPosition(int position) {
            return position - getHeaderCount();
        }

        @Override
        public int getItemCount() {
            return getHeaderCount() + adapter.getItemCount() + getFooterCount();
        }
        private View getHeader() {
            return header;
        }

        private View getFooter() {
            return footer;
        }

        public int getHeaderCount() {
            return header == null ? 0 : 1;
        }

        public int getFooterCount() {
            return footer == null ? 0 : 1;
        }

        /**
         * 让 header / footer / loading more 在gridlayoutManager里跨越多列展示，宽度达到全屏， 其他单列
         *
         * @param spanCount the number of spans
         * @return a new GridLayoutManager.SpanSizeLookup to be used with this Adapter
         */
        public GridLayoutManager.SpanSizeLookup createSpanSizeLookup(int spanCount) {
            return new CustomSpanSizeLookup(null, spanCount);
        }

        private final class CustomSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
            private final GridLayoutManager.SpanSizeLookup source;
            private final int spanCount;

            private CustomSpanSizeLookup(GridLayoutManager.SpanSizeLookup source, int spanCount) {
                this.source = source;
                this.spanCount = spanCount;
            }

            @Override
            public int getSpanSize(int position) {

                if (isFullSpan(position)) {
                    return spanCount;
                } else {
                    return 1;
                }
            }
        }

    }

    public static class AdvanceHolder extends RecyclerView.ViewHolder {
        public AdvanceHolder(View itemView) {
            super(itemView);
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position, long id);
    }

    public static interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView parent, View view, int position, long id);
    }
}
