package com.lib.pulltorefreshview.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class PullableGridView extends GridView implements Pullable {
    private boolean disablePullDown;    //禁用下拉刷新
    private boolean disablePullUp;        //禁用上拉加载

    public PullableGridView(Context context) {
        super(context);
    }

    public PullableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (!disablePullDown && getCount() == 0) {
            //没有item的时候也可以下拉刷新
            return true;
        } else if (!disablePullDown && getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0) {
            //滑到顶部了
            return true;
        } else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (!disablePullUp && getCount() == 0) {
            //没有item的时候也可以上拉加载
            return true;
        } else if (!disablePullUp && getLastVisiblePosition() == (getCount() - 1)) {
            //滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(
                    getLastVisiblePosition()
                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }

    /**
     * 禁用下拉刷新
     *
     * @param disable
     */
    public void setPullDown(boolean disable) {
        this.disablePullDown = disable;
    }

    /**
     * 禁用上拉刷新
     *
     * @param disable
     */
    public void setPullUp(boolean disable) {
        this.disablePullUp = disable;
    }
}
