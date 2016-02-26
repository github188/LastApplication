package com.iermu.ui.adapter;

//

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.AlarmDeviceItem;
import com.iermu.client.model.AlarmImageData;
import com.iermu.client.model.CamLive;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.util.Util;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by zhoushaopei on 15/6/27.
 */
public class MineMessageAdapter extends BaseAdapter {
    private Context context;
    private List<AlarmDeviceItem> deviceItems = new ArrayList<AlarmDeviceItem>();
    private int imageWidth;

    public MineMessageAdapter(FragmentActivity ctx) {
        this.context = ctx;
        DisplayMetrics metric = new DisplayMetrics();
        ctx.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        imageWidth = metric.widthPixels - Util.DensityUtil.dip2px(context, 100);
    }

    public void setDeviceItems(List<AlarmDeviceItem> deviceItems) {
        if (deviceItems == null) {
            deviceItems = new ArrayList<AlarmDeviceItem>();
        }
        this.deviceItems = deviceItems;
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
            convertView = View.inflate(context, R.layout.item_message, null);
            holder.num = (TextView) convertView.findViewById(R.id.num);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.alarm_img = (ImageView) convertView.findViewById(R.id.alarm_img);
            holder.viewNum = convertView.findViewById(R.id.viewNum);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AlarmDeviceItem deviceItem = getItem(position);
        AlarmImageData imageData = deviceItem.getImageData();
        // 设置图片高度
        LinearLayout.LayoutParams layoutParamsImage = (LinearLayout.LayoutParams) holder.alarm_img.getLayoutParams();
        layoutParamsImage.width = imageWidth;
        int height = imageWidth * 9 / 16;
        layoutParamsImage.height = height;
        holder.alarm_img.setLayoutParams(layoutParamsImage);

        // 设置文字区域高度
        LinearLayout.LayoutParams layoutParamsNum = (LinearLayout.LayoutParams) holder.viewNum.getLayoutParams();
        layoutParamsNum.width = Util.DensityUtil.dip2px(context, 48);
        layoutParamsNum.height = height;
        holder.viewNum.setLayoutParams(layoutParamsNum);

        Picasso.with(context).load(imageData.getImageUrl()).into(holder.alarm_img);
        String framesChange = context.getResources().getString(R.string.frames_change);
        holder.time.setText(convertTime(imageData.getAlarmtime()) + framesChange);
        holder.num.setText(deviceItem.getCount() + "");
        holder.name.setText(deviceItem.getDeviceName());

        if (deviceItem.isHasNew()) {
            holder.viewNum.setBackgroundColor(0xfff3993e);
        } else {
            holder.viewNum.setBackgroundColor(0xff00acef);
        }

        return convertView;
    }

    private String convertTime(String oldTime) {
        String language = LanguageUtil.getLanguage();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(language.equals("zh")? "MM月dd号HH:mm" : "HH:mm MM/dd ");
        String newTime = "";
        try {
            Date date = dateFormat.parse(oldTime);
            newTime = dateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }

    class ViewHolder {
        TextView num;
        TextView name;
        TextView time;
        ImageView alarm_img;
        View viewNum;
    }
}
