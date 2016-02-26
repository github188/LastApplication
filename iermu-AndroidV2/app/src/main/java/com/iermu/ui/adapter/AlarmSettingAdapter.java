package com.iermu.ui.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnAlarmChangeListener;
import com.iermu.client.model.AlarmDeviceItem;
import com.iermu.client.model.AlarmImageData;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.util.Util;
import com.squareup.picasso.Picasso;
import com.viewinject.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by zhoushaopei on 15/6/27.
 */
public class AlarmSettingAdapter extends BaseAdapter {
    private Context context;
    private List<AlarmDeviceItem> deviceItems = new ArrayList<AlarmDeviceItem>();
    private OnButtonClickListener listener;
    CronRepeat repeat;
    String week;
    String time;
    public AlarmSettingAdapter(FragmentActivity ctx) {
        this.context = ctx;
    }

    public void setDeviceItems(List<AlarmDeviceItem> deviceItems) {
        if (deviceItems == null) {
            deviceItems = new ArrayList<AlarmDeviceItem>();
        }
        this.deviceItems = deviceItems;
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return deviceItems.size();
    }

    @Override
    public AlarmDeviceItem getItem(int position) {
        return deviceItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_message_setting, null);
            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textviewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            holder.viewSetAlarm = convertView.findViewById(R.id.viewSetAlarm);
            holder.viewOpenAlarm = convertView.findViewById(R.id.viewOpenAlarm);
            holder.viewGotoOpen = convertView.findViewById(R.id.alarm_start);
            holder.viewClose = convertView.findViewById(R.id.viewClose);
            holder.viewReset = convertView.findViewById(R.id.viewReset);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final int finalPosition = position;
        holder.viewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCloseClick(finalPosition);
                }
            }
        });

        holder.viewReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onResetClick(finalPosition);
                }
            }
        });

        holder.viewGotoOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onGotoOpenClick(finalPosition);
                }
            }
        });

        AlarmDeviceItem deviceItem = getItem(position);

        String deviceId = deviceItem.getDeviceId();
        CamCron camCron = ErmuBusiness.getCamSettingBusiness().getAlarmCron(deviceId);
        if (camCron != null){
            repeat = camCron.getRepeat();
            Date startDate = camCron.getStart();
            Date endDate = camCron.getEnd();
            String startTime = getCurrentTime(startDate);
            String endTime = getCurrentTime(endDate);
            time =  context.getResources().getString(R.string.form_time) + " " + startTime + " "  + context.getResources().getString(R.string.to_time) + " " + endTime + " " ;
        }
        if (repeat != null){
             week = getWeek(repeat);
        }

        if (deviceItem.isAlarmIsOpen()) {
            holder.viewOpenAlarm.setVisibility(View.GONE);
            holder.viewSetAlarm.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(week) || TextUtils.isEmpty(time)) {
                holder.textviewTime.setText(R.string.every_day_home);
            }else  {
                String str = context.getResources().getString(R.string.every_day);
                String mLookHome = context.getResources().getString(R.string.look_home);
                String weekTxt = context.getResources().getString(R.string.week);
                if (str.equals(week)){
                    holder.textviewTime.setText(week+"，"+time + mLookHome);
                }else {
                    holder.textviewTime.setText(weekTxt + week+"，"+time+ mLookHome);
                }
            }
        } else {
            holder.viewOpenAlarm.setVisibility(View.VISIBLE);
            holder.viewSetAlarm.setVisibility(View.GONE);
            holder.textviewTime.setText(R.string.alarm_no_open);
        }

        holder.textViewName.setText(deviceItem.getDeviceName());

        return convertView;
    }

    class ViewHolder {
        TextView textViewName;
        TextView textviewTime;
        View viewSetAlarm;
        View viewOpenAlarm;
        View viewGotoOpen;
        View viewClose;
        View viewReset;
    }

    private String getWeek(CronRepeat repeat) {
        StringBuilder sb = new StringBuilder();
        boolean workDay = repeat.isMonday() && repeat.isTuesday() && repeat.isWednesday() && repeat.isThursday() && repeat.isFriday();
        boolean weekend = repeat.isSaturday() && repeat.isSunday();
        String str = context.getString(R.string.every_day);
        String mWorkDay = context.getString(R.string.work_day);
        if (workDay && weekend) {
            sb.append(str);
        } else if (workDay && !repeat.isSaturday() && !repeat.isSunday()) {
            sb.append(mWorkDay);
        } else {
            sb.append((weekend && workDay) ? context.getString(R.string.week_txt_null) : "")
                    .append(repeat.isMonday() ? context.getString(R.string.clip_mon)+" " : "")
                    .append(repeat.isTuesday() ? context.getString(R.string.clip_tue)+" " : "")
                    .append(repeat.isWednesday() ? context.getString(R.string.clip_wed)+" " : "")
                    .append(repeat.isThursday() ? context.getString(R.string.clip_thu)+" " : "")
                    .append(repeat.isFriday() ? context.getString(R.string.clip_fri)+" " : "")
                    .append(repeat.isSaturday() ? context.getString(R.string.clip_sat)+" " : "")
                    .append(repeat.isSunday() ? context.getString(R.string.clip_sun)+" " : "");
            int length = sb.length();
            if (length > 0) {
                String language = LanguageUtil.getLanguage();
                if (language.equals("zh")) {
                    sb.deleteCharAt((length>=2) ? length-2: length-1);//减去中文末尾的顿号和空格
                }
            }
        }
        return sb.toString();
    }
    private String getCurrentTime(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String format = sdf.format(data);
        return format;
    }

    /**
     * item按钮回调
     */
    public interface OnButtonClickListener {
        void onCloseClick(int position);

        void onResetClick(int position);

        void onGotoOpenClick(int position);

    }
}
