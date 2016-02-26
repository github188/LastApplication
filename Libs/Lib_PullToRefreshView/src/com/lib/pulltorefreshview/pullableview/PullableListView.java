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

	@Override
	public boolean canPullDown()
	{
		if (getCount() == 0)
		{
			// û��item��ʱ��Ҳ��������ˢ��
			return true;
		} else if (getFirstVisiblePosition() == 0
				&& getChildAt(0).getTop() >= 0)
		{
			// ����ListView�Ķ�����
			return true;
		} else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		if (getCount() == 0)
		{
			// û��item��ʱ��Ҳ������������
			return true;
		} else if (getLastVisiblePosition() == (getCount() - 1))
		{
			// �����ײ���
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
		if(adapter != null ) {
			checkFocus();
		}
		super.setAdapter(adapter);
		if(adapter != null ) {
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
            	if(parent != null) {
                	parent.setVisibility(View.INVISIBLE);
            	} else  {
            		setVisibility(View.INVISIBLE);
            	}
            } else {
                // If the caller just removed our empty view, make sure the list view is visible
            	if(parent != null) {
                	parent.setVisibility(View.VISIBLE);
            	} else  {
            		setVisibility(View.VISIBLE);
            	}
            }
        } else {
            if (getEmptyView() != null) getEmptyView().setVisibility(View.GONE);
            if(parent != null) {
            	parent.setVisibility(View.VISIBLE);
        	} else  {
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
