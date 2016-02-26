package com.iermu.ui.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cms.iermu.cmsUtils;
import com.iermu.R;
import com.iermu.client.business.impl.setupdev.WifiNetworkManager;
import com.iermu.client.util.WifiUtil;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoushaopei on 15/6/23.
 */
public class WifiAdapter extends BaseAdapter {

    private Context ctx;
    private List<ScanResult> mList;
    private String ssid;
    private static final int IPC_SET_FAIL = -1;    // 设备配置失败

    public WifiAdapter(Context context) {
        this.mList = new ArrayList<ScanResult>();
        this.ctx = context;
        WifiNetworkManager manager = WifiNetworkManager.getInstance(context);
        this.ssid = manager.getSSIDStr();
    }

    @Override
    public int getCount() {
        return mList.size();
    }


    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private int getWifiImg(int iSignal, boolean bOpen){
        int ret = -1;
        if(iSignal>=-45){ // 4
            ret = ( bOpen? R.drawable.ic_wifi_signal_4 : R.drawable.ic_wifi_lock_signal_3);
        }
        else if (iSignal>=-55){ // 3
            ret = (bOpen? R.drawable.ic_wifi_signal_3 : R.drawable.ic_wifi_lock_signal_3);
        }
        else if (iSignal>=-65){ // 2
            ret = (bOpen? R.drawable.ic_wifi_signal_2 : R.drawable.ic_wifi_lock_signal_2);
        }
        else{  // 1
            ret = (bOpen? R.drawable.ic_wifi_signal_1 : R.drawable.ic_wifi_lock_signal_1);
        }
        return ret;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(ctx, R.layout.fragment_wifi_item, null);
            holder.wifi_num = (TextView) convertView.findViewById(R.id.wifi_num);
            holder.not_use = (TextView)convertView.findViewById(R.id.not_use);
            holder.wifi_signal = (ImageView) convertView.findViewById(R.id.wifi_signal);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanResult item = (ScanResult) getItem(position);
        boolean bOpen = cmsUtils.getOpenWifi(mList.get(position).capabilities);
        int iLevel = item.level;
        holder.wifi_signal.setImageResource(getWifiImg(iLevel, bOpen));

        if (ssid.equals(item.SSID)) {
            holder.wifi_num.setTextColor(ctx.getResources().getColor(R.color.mycam_talk));
            holder.not_use.setVisibility(WifiUtil.is5GHz(item.frequency) ? View.VISIBLE:View.GONE);
//            holder.not_use.setVisibility(View.GONE);
        } else if (WifiUtil.is5GHz(item.frequency)) {
            holder.wifi_num.setTextColor(ctx.getResources().getColor(R.color.wifi_bg));
            holder.not_use.setVisibility(View.VISIBLE);
        } else {
            holder.wifi_num.setTextColor(ctx.getResources().getColor(R.color.devices_help_btn));
            holder.not_use.setVisibility(View.GONE);
        }
        holder.wifi_num.setText(item.SSID);
        return convertView;
    }

    //刷新数据
    public void notifyDataSetChanged(List<ScanResult> list) {
        if (list == null) {
            list = new ArrayList<ScanResult>();
        }
        this.mList = list;
        super.notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView wifi_num;
        ImageView wifi_signal;
        TextView not_use;
    }
}
