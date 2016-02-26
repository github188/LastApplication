package com.iermu.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Created by zhangxq on 15/8/27.
 */
public class TouchEventImageButton extends ImageButton {
    TouchEventListener listener;

    public TouchEventImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchEventImageButton(Context context) {
        super(context);
    }

    public void setTouchEventListener(TouchEventListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (listener != null) {
                listener.onTouchEventChange(MotionEvent.ACTION_DOWN);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (listener != null) {
                listener.onTouchEventChange(MotionEvent.ACTION_UP);
            }
        }
        return super.onTouchEvent(event);
    }

    public interface TouchEventListener {
        void onTouchEventChange(int eventType);
    }
}
