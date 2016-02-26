package com.iermu.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.lib.pulltorefreshview.pullableview.Pullable;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by zhoushaopei on 16/1/6.
 */
public class PullableStickyListHeadersListView extends StickyListHeadersListView implements Pullable {

    public PullableStickyListHeadersListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableStickyListHeadersListView(Context context) {
        super(context);
    }

    public PullableStickyListHeadersListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        ListView listView = getWrappedList();
        if (getCount() == 0 || listView==null) { //没有item的时候也可以下拉刷新
            return true;
        } else if (listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() >= 0) { //滑到顶部了
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canPullUp() {
        ListView listView = getWrappedList();
        if (getCount() == 0 || listView==null) {
            //没有item的时候也可以上拉加载
            return true;
        } else if (listView.getLastVisiblePosition() == (getCount() - 1)) {
            //滑到底部了
            if (listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition()) != null
                    && listView.getChildAt(
                    listView.getLastVisiblePosition() - listView.getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }
}
