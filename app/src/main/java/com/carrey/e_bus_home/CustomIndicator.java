package com.carrey.e_bus_home;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义圆点
 * CustomIndicator
 * tanghaihua
 * 2015年4月2日 上午10:11:04
 * @version 1.0
 */
public class CustomIndicator extends LinearLayout {

	private Context mContext;
	private int width;
	private int height;
	private int margin;
	private Drawable normalDrawable, selectedDrawable;
	private int count = 0;
	private int currentCount = 0;
	private List<ImageView> views = new ArrayList<ImageView>();
	
	public CustomIndicator(Context context) {
        super(context);
        mContext = context;
	}
	
	public CustomIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CustomIndicator);
		margin = (int) a.getDimension(R.styleable.CustomIndicator_margin, 0) == 0
                ? UIUtil.dip2px(8) : (int) a.getDimension(R.styleable.CustomIndicator_margin, 0);
		width = (int) a.getDimension(R.styleable.CustomIndicator_width, 0) == 0 
		        ? UIUtil.dip2px(7) : (int) a.getDimension(R.styleable.CustomIndicator_width, 0);
		height = (int) a.getDimension(R.styleable.CustomIndicator_height1, 0) == 0 
                ? UIUtil.dip2px(7) : (int) a.getDimension(R.styleable.CustomIndicator_height1, 0);
		count = a.getInteger(R.styleable.CustomIndicator_count, 0);
		normalDrawable = a.getDrawable(R.styleable.CustomIndicator_normal_icon) == null
		        ? mContext.getResources().getDrawable(R.drawable.shape_indictor) : a.getDrawable(R.styleable.CustomIndicator_normal_icon);
		selectedDrawable = a.getDrawable(R.styleable.CustomIndicator_selected_icon) == null
                ? mContext.getResources().getDrawable(R.drawable.shape_indictor_select) : a.getDrawable(R.styleable.CustomIndicator_selected_icon);
		a.recycle();
		initViews();
	}
	
	/**
	 * 设置当前选中圆点
	 * setCurrentPosition
	 * @param pos
	 * @since 1.0
	 */
	@SuppressWarnings("deprecation")
    public void setCurrentPosition(int pos) {
		currentCount = pos;
		if(currentCount < 0) {
			currentCount = 0;
		}
		if(currentCount > count-1) {
			currentCount = count-1;
		}
		
		if(views.size() > 0){
		    for(int i = 0; i < count; i++) {
	            views.get(i).setBackgroundDrawable(normalDrawable);
	        }
	        views.get(currentCount).setBackgroundDrawable(selectedDrawable);
        }
	}
	
	/**
	 * 下一页
	 * next
	 * @since 1.0
	 */
	public void next() {
		setCurrentPosition(currentCount + 1);
	}
	
	/**
	 * 上一页
	 * previous
	 * @since 1.0
	 */
	public void previous() {
		setCurrentPosition(currentCount - 1);
	}
	
	/**
	 * 设置圆点个数
	 * setCount
	 * @param count
	 * @since 1.0
	 */
	public void setCount(int count) {
		this.count = count;
		this.currentCount = 0;
		initViews();
	}
	
	@SuppressWarnings("deprecation")
    private void initViews() {
	    if(count != 0){
			this.removeAllViews();
	        views.clear();
	        for(int i = 0; i < count; i++) {
	            ImageView view = new ImageView(mContext);
	            views.add(view);
	            LayoutParams params = new LayoutParams(width == 0 ? LayoutParams.WRAP_CONTENT : width, 
	                    height == 0 ? LayoutParams.WRAP_CONTENT : height);
	            if(i != count-1) {
	                params.rightMargin = margin;
	            }
	            view.setLayoutParams(params);
	            view.setBackgroundDrawable(normalDrawable);
	            this.addView(view);
	        }
	        setCurrentPosition(0);
	    }
	}
	
}
