package com.iermu.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.iermu.R;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.view.SwitchButtonNew;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xjy on 15/7/27.
 */
public class CloudServiceAdapter extends BaseAdapter {

    private static final int VIEWTYPE_TITLE = 0;
    private static final int VIEWTYPE_ITEM  = 2;
    private Context context;
    private List<String> deviceIds = new ArrayList<String>();
    private Map<String, List<CamLive>> map = new LinkedTreeMap<String, List<CamLive>>();
    private String noOutOfDate;
    private String outOfDate;
    private String inDate;

    public CloudServiceAdapter(Context ctx){
        this.map = new LinkedTreeMap<String, List<CamLive>>();
        this.context = ctx;
        this.deviceIds = new ArrayList<String>();
        noOutOfDate = context.getResources().getString(R.string.no_out_of_date);
        outOfDate = context.getResources().getString(R.string.out_of_date);
        inDate = context.getResources().getString(R.string.in_date);
        map.put(noOutOfDate, new ArrayList<CamLive>());
        map.put(outOfDate, new ArrayList<CamLive>());
    }

    @Override
    public int getCount() {
        int count = 0;
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()) {
            String next = iterator.next();
            List<?> list = map.get(next);
            if(list.size() > 0) {
                count += list.size() + 1;
            }
        }
        return count;
    }
    @Override
    public Object getItem(int position) {
        int count = 0;
        int tempPos = position;
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()) {
            String next = iterator.next();
            List<?> list = map.get(next);
            if(list.size() <= 0) {
                continue;
            }
            int temp = list.size();// + 1
            tempPos -= 1;
            count += temp + 1;

            if(list.size() >0 && position == count - temp -1) {//position == count ||
                return next;
//            } else if(count >= position) {
            } else if(tempPos>=0 && list.size()>0 && temp > tempPos) {
                return list.get(tempPos);
            }
            tempPos -= temp;
            tempPos = (tempPos<0) ? position : tempPos;
        }
        return null;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()) {
            String next = iterator.next();
            List<?> list = map.get(next);
            if(list.size() <= 0) {
                continue;
            }
            int temp = list.size() + 1;
            count += temp;
            if(list.size()>0 && (position == count-temp || position == count)) {
                return VIEWTYPE_TITLE;
            } else if(list.size()>0 && count >= position) {
                return VIEWTYPE_ITEM;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
//        if (convertView == null) {
        holder = new ViewHolder();
        convertView = View.inflate(context, R.layout.cloud_state_item, null);
        holder.description = (TextView) convertView.findViewById(R.id.description);
        holder.deviceid = (TextView) convertView.findViewById(R.id.deviceid);
        holder.end_time = (TextView) convertView.findViewById(R.id.end_time);
        holder.buy_cloud = (TextView) convertView.findViewById(R.id.buy_cloud);
        holder.img = (ImageView) convertView.findViewById(R.id.img);
        holder.switch_button = (SwitchButtonNew) convertView.findViewById(R.id.swith_button);
        convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }

        int itemViewType = getItemViewType(position);
        switch(itemViewType) {
            case VIEWTYPE_TITLE:
                String key = (String) getItem(position);
                TextView text = new TextView(context);
                text.setText(key);
                text.setTextColor(Color.parseColor("#666666"));
                text.setPadding(40, 40, 0, 20);
                text.setTextSize(18);
                convertView = text;
                break;
            case VIEWTYPE_ITEM:
                CamLive item = (CamLive) getItem(position);
                final String deviceId = item.getDeviceId();
                long cvrEndTime = item.getCvrEndTime()*1000;
//                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                Date date= new Date(cvrEndTime);

//                return sdf.format(date);
                Date time = (item!=null) ? date : new Date();
                final String devDesc = item.getDescription();
                long curTime = new Date().getTime();
                boolean visible = (curTime <= time.getTime());

                holder.description.setText(devDesc);
                holder.deviceid.setText(deviceId);
                holder.end_time.setText(inDate + StrToDate(time));
                holder.end_time.setVisibility(visible ? View.VISIBLE : View.GONE);
                holder.img.setBackgroundResource(visible ? R.drawable.setting_location : R.drawable.user_icon_sale);

//                final WebCamCamerasDb dbHelper = new WebCamCamerasDb(context);
//                dbHelper.open();
//                DevRow dev = dbHelper.fetchDevByNatID(deviceId);
//                final boolean bOn = !(dev!=null && dev.rec_expire_on!=null && dev.rec_expire_on.equals("false"));
//                dbHelper.close();
//                holder.switch_button.setSwitchOn(bOn);

                holder.switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        setNofi(deviceId, devDesc, isChecked);
                    }
                });
                holder.buy_cloud.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buyCloud(deviceId);
                    }
                });
                break;
        }
        return convertView;

    }

    class ViewHolder {
        TextView description;
        TextView deviceid;
        TextView end_time;
        TextView buy_cloud;
        ImageView img;
        SwitchButtonNew switch_button;
    }

    public void notifyData(List<CamLive> camCloud) {
        if (camCloud == null) {
            camCloud = new ArrayList<CamLive>();
        }
        long curTime = new Date().getTime();
        for (CamLive item : camCloud) {
            int connectType = item.getConnectType();
            if (connectType == ConnectType.BAIDU) {
                String deviceId = item.getDeviceId();
                long cvrEndTime = item.getCvrEndTime()*1000;
                if(curTime <= cvrEndTime) {
                    //有效期
                    List<CamLive> camLives = map.get(noOutOfDate);
                    if(camLives == null) {
                        camLives = new ArrayList<CamLive>();
                        map.put(noOutOfDate, camLives);
                    }
                    if( !deviceIds.contains(deviceId)) {
                        camLives.add(item);
                    }
                } else {
                    //过期
                    List<CamLive> camLives = map.get(outOfDate);
                    if(camLives == null) {
                        camLives = new ArrayList<CamLive>();
                        map.put(outOfDate, camLives);
                    }
                    if( !deviceIds.contains(deviceId)) {
                        camLives.add(item);
                    }
                }
                deviceIds.add(item.getDeviceId());
            }
        }
        notifyDataSetChanged();
    }

    public String StrToDate(Date date) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(date);
    }

    private void buyCloud(String deviceId) {
////        final String accessToken = ErmuApplication.getAccessToken();
//        Intent intent = new Intent(context, WebActivity.class);
//        intent.setAction("android.intent.action.VIEW");
//        Uri content_url = Uri.parse("http://kankan.baidu.com/mobile/#/buy/access_token" + "=" +
////                accessToken
//                 "&deviceid=" + deviceId);
//        intent.setData(content_url);
//        context.startActivity(intent);
        WebActivity.actionStartWeb(context, WebActivity.PAGE_CVRBUY);
    }

//    private void setNofi(String deviceId, String devDesc, boolean isChecked) {
//        WebCamCamerasDb dbHelper = new WebCamCamerasDb(context);
//        dbHelper.open();
//        DevRow dev = dbHelper.fetchDevByNatID(deviceId);
//        if(dev==null){
//            //cmsUtils.baiduUserStruct bdUser = MainActivity.getLogUser();
//            String strUid = "";
//            //if(bdUser!=null) strUid = bdUser.strUID;
//            dev = new DevRow();
//            dev.nat_id = deviceId;
//            dev.name = devDesc;
//            dev.password = strUid;
//            dev.type = cmsConstants.CMS_BD_IERMU;
//            dev.url = "";
//            dev.rec_expire_on = Boolean.toString(isChecked);
//            dev.liveplay_mode = Integer.toString(cmsConstants.LIVE_LAN_BD); // 正常播放模式
//            dbHelper.createRow(dev, WebCamCamerasDb.TABLE_DEVS);
//        }
//        else{
//            dev.rec_expire_on = Boolean.toString(isChecked);
//            dbHelper.updateRow(dev, WebCamCamerasDb.TABLE_DEVS);
//        }
//        dbHelper.close();
//    }
}
