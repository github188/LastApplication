package com.lib.pulltorefreshview.pullableview;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PullableListView extends ListView implements Pullable {

    private PullableDataSetObserver dataSetObserver;
    private boolean disablePullDown;    //禁用下拉刷新
    private boolean disablePullUp;        //禁用上拉加载

    public PullableListView(Context context) {
        super(context);
        init();
    }

    public PullableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        dataSetObserver = new PullableDataSetObserver();
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

    @Override
    public boolean canPullDown() {
        if (!disablePullDown && getCount() == 0) {
            //没有item的时候也可以下拉刷新
            return true;
        } else if (!disablePullDown && getFirstVisiblePosition() == 0
                && (getChildAt(0) != null && getChildAt(0).getTop() >= 0)) {
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

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter != null) {
            checkFocus();
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            checkFocus();
            adapter.registerDataSetObserver(dataSetObserver);
        }
    }

    void checkFocus() {
        final Adapter adapter = getAdapter();
        // The order in which we set focusable in touch mode/focusable may matter
        // for the client, see View.setFocusableInTouchMode() comments for more
        // details
        if (getEmptyView() != null) {
            updateEmptyStatus((adapter == null) || adapter.isEmpty());
        }
    }

    private void updateEmptyStatus(boolean empty) {
        if (isInFilterMode()) {
            empty = false;
        }

        ViewGroup parent = null;//(ViewGroup) getParent();
        if (empty) {
            if (getEmptyView() != null) {
                getEmptyView().setVisibility(View.VISIBLE);
                if (parent != null) {
                    parent.setVisibility(View.INVISIBLE);
                } else {
                    setVisibility(View.INVISIBLE);
                }
            } else {
                // If the caller just removed our empty view, make sure the list view is visible
                if (parent != null) {
                    parent.setVisibility(View.VISIBLE);
                } else {
                    setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (getEmptyView() != null) getEmptyView().setVisibility(View.GONE);
            if (parent != null) {
                parent.setVisibility(View.VISIBLE);
            } else {
                setVisibility(View.VISIBLE);
            }
        }
    }

    class PullableDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            checkFocus();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            checkFocus();
            requestLayout();
        }
    }
}
