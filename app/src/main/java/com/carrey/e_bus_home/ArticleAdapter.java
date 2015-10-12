package com.carrey.e_bus_home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * 类描述：文章列表适配器
 * 创建人：tanghaihua
 * 创建时间：2015/7/1 14:40
 */
public class ArticleAdapter extends BaseAdapter<String, RecyclerView.ViewHolder> {
//    private BitmapTools mBitmapTools;

    public ArticleAdapter(Context context, List<String> list) {
        super(context, list);
//        mBitmapTools = new BitmapTools(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ArticleSmallHolder(new TextView(mContext));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

    }

    public static class ArticleSmallHolder extends RecyclerView.ViewHolder {


        public ArticleSmallHolder(View view) {
            super(view);
        }
    }

}
