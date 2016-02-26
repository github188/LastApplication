package com.iermu.ui.view.kankan.wheel.widget.adapters;

import android.content.Context;

import java.util.List;

/**
 * Created by zhangxq on 15/12/31.
 */
public class ListWheelAdapter<T> extends AbstractWheelTextAdapter {
    private List<T> list;

    public ListWheelAdapter(Context context, int itemResource, int itemTextResource, List<T> list) {
        super(context, itemResource, itemTextResource);
        this.list = list;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < list.size()) {
            T item = list.get(index);
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return list.size();
    }
}
