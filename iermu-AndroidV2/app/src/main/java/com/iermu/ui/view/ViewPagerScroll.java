package com.iermu.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhoushaopei on 16/1/6.
 */
public class ViewPagerScroll extends ViewPager {

    private boolean scrollble = true;

    public ViewPagerScroll(Context context) {
        super(context);
    }

    public ViewPagerScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollble) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }
}
