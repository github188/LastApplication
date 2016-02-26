package com.iermu.ui.adapter;

//

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.CamDate;
import com.iermu.client.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by zhoushaopei on 15/6/27.
 */
public class RecordDateGridAdapter extends BaseAdapter {
    public static final int TYPE_FRAGMENT = 1;
    public static final int TYPE_CONTROLLER = 2;

    private FragmentActivity context;
    private List<CamDate> dates = new ArrayList<CamDate>();
    private int selectPosition = 0;

    private int adapterType = TYPE_FRAGMENT;

    public RecordDateGridAdapter(FragmentActivity context) {
        this.context = context;
        this.dates = new ArrayList<CamDate>();
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    public void setDates(List<CamDate> list) {
        if (list != null) {
            dates = list;
        } else {
            dates = new ArrayList<CamDate>();
        }
    }

    public void setAdapterType(int adapterType) {
        this.adapterType = adapterType;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    @Override
    public CamDate getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (adapterType == TYPE_FRAGMENT) {
            convertView = View.inflate(context, R.layout.item_record_date_grid, null);
        } else {
            convertView = View.inflate(context, R.layout.item_record_date_grid_ctrl, null);
        }

        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        TextView textViewToday = (TextView) convertView.findViewById(R.id.textViewToday);

        CamDate camDate = getItem(position);
        Date date = new Date(camDate.getDayStartTime() * 1000l);
        String dateStr = DateUtil.formatDate(date, DateUtil.LONG_DATE_FORMAT);
        textViewDate.setText(dateStr);
        if (!camDate.isExistRecord()) {
            if (adapterType == TYPE_FRAGMENT) {
                convertView.setBackgroundResource(R.drawable.record_date_select_bg_normal);
                textViewToday.setTextColor(0xffb2b2b2);
                textViewDate.setTextColor(0xffb2b2b2);
            } else {
                textViewToday.setTextColor(0xff636363);
                textViewDate.setTextColor(0xff636363);
            }
        } else {
            if (adapterType == TYPE_FRAGMENT) {
                convertView.setBackgroundResource(R.drawable.record_date_select_bg);
                if (position == selectPosition) {
                    textViewToday.setTextColor(0xff00acef);
                    textViewDate.setTextColor(0xff00acef);
                } else {
                    textViewToday.setTextColor(0xff666666);
                    textViewDate.setTextColor(0xff666666);
                }
            } else {
                if (position == selectPosition) {
                    textViewToday.setTextColor(0xff00acef);
                    textViewDate.setTextColor(0xff00acef);
                } else {
                    textViewToday.setTextColor(0xfffefefe);
                    textViewDate.setTextColor(0xfffefefe);
                }
            }
        }

        if (position == 0) {
            textViewToday.setText(R.string.today);
        } else if (position == 1) {
            textViewToday.setText(R.string.yesterday);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case 2:
                    textViewToday.setText(R.string.monday);
                    break;
                case 3:
                    textViewToday.setText(R.string.tuesday);
                    break;
                case 4:
                    textViewToday.setText(R.string.wednesday);
                    break;
                case 5:
                    textViewToday.setText(R.string.thursday);
                    break;
                case 6:
                    textViewToday.setText(R.string.friday);
                    break;
                case 7:
                    textViewToday.setText(R.string.saturday);
                    break;
                case 1:
                    textViewToday.setText(R.string.sunday);
                    break;
            }
        }
        return convertView;
    }
}
