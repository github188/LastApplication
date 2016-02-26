package com.iermu.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.CamOnlineStatus;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.ui.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhoushaopei on 15/6/26.
 */
public class PublicChannelAdapter extends BaseAdapter {

    private List<CamLive> mList;
    private Activity ctx;
    private int imageWidth;

    public PublicChannelAdapter(Activity context, List<CamLive> list) {
        if (list == null) {
            list = new ArrayList<CamLive>();
        }
        this.mList = list;
        this.ctx = context;
        initImageWidth();
    }

    private void initImageWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        ctx.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels - Util.DensityUtil.dip2px(ctx, 16);
        imageWidth = width;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CamLive getItem(int position) {
        return mList.get(position);
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
            convertView = View.inflate(ctx, R.layout.item_publiccam_item, null);
            holder.cam_where = (TextView) convertView.findViewById(R.id.cam_where);
            holder.cam_name = (TextView) convertView.findViewById(R.id.cam_name);
            holder.cam_img = (ImageView) convertView.findViewById(R.id.auth_cam_img);
            holder.cam_state = (TextView) convertView.findViewById(R.id.cam_state);
            holder.cam_num_people = (TextView) convertView.findViewById(R.id.cam_num_people);
            holder.avator_img = (CircleImageView) convertView.findViewById(R.id.avator_img);
            holder.lyy_logo_ = (ImageView) convertView.findViewById(R.id.lyy_logo_);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CamLive item = getItem(position);
        int type = item.getConnectType();
        if (type == ConnectType.LINYANG) {
            holder.lyy_logo_.setVisibility(View.VISIBLE);
        } else {
            holder.lyy_logo_.setVisibility(View.INVISIBLE);
        }
        holder.cam_name.setText(item.getOwnerName());
        holder.cam_where.setText(item.getDescription());
        if (item.isOffline()) {
            holder.cam_state.setText(R.string.off_line);
        } else if (item.isPowerOn()) {
            holder.cam_state.setText(R.string.public_living);
        } else {
            holder.cam_state.setText(R.string.close);
        }
        String peoplesLook = ctx.getResources().getString(R.string.peoples_look);
        holder.cam_num_people.setText(item.getPersonNum() + peoplesLook);
        String thumbnail = TextUtils.isEmpty(item.getThumbnail()) ? "default" : item.getThumbnail();
        String avator = TextUtils.isEmpty(item.getAvator()) ? "default" : item.getAvator();
        ViewGroup.LayoutParams params = holder.cam_img.getLayoutParams();
        params.width = imageWidth;
        params.height = imageWidth * 9 / 16;
        holder.cam_img.setLayoutParams(params);
        Picasso.with(ctx)
                .load(thumbnail)
                .placeholder(R.drawable.iermu_thumb)
                .into(holder.cam_img);
        Picasso.with(ctx)
                .load(avator)
                .placeholder(R.drawable.avator_img)
                .into(holder.avator_img);
        return convertView;
    }


    /**
     * 获取当前屏幕显示的DeviceIds
     *
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     * @return
     */
    public List<String> getItemIds(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        List<String> deviceIds = new ArrayList<String>();
        if (getCount() > 0) {
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount - 1; i++) {
                CamLive item = (CamLive) getItem(i);
                deviceIds.add(item.getDeviceId());

            }
        }
        Logger.i(" first:" + firstVisibleItem + " visible:" + visibleItemCount + " total:" + totalItemCount + " deviceIds:" + deviceIds.toString());
        return deviceIds;
    }

    public void notifyDataSetChanged(List<CamLive> list) {
        if (list == null) {
            this.mList = new ArrayList<CamLive>();
        }
        this.mList = list;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView cam_where;
        TextView cam_name;
        ImageView cam_img;
        TextView cam_state;
        TextView cam_num_people;
        CircleImageView avator_img;
        ImageView lyy_logo_;
    }

}
