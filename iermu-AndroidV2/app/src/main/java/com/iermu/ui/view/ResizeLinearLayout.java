package com.iermu.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 键盘弹出|隐藏Layout产生变化
 * @author wcy
 *
 */
public class ResizeLinearLayout extends LinearLayout {

	private OnResizeListener listener;

	public ResizeLinearLayout(Context context) {
		super(context);
	}

	public ResizeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnResizeListener(OnResizeListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(listener != null){
			listener.onResize(w, h, oldw, oldh);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

    public interface OnResizeListener {
        public void onResize(int w, int h, int oldw, int oldh);
    }
}
