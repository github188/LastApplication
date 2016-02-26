package com.iermu.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iermu.R;
import com.iermu.client.model.AlarmImageData;
import com.iermu.ui.util.Util;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxq on 15/12/9.
 */
public class MessageGridView extends LinearLayout {
    @ViewInject(R.id.gridView)
    GridView gridView;

    private Context context;
    private List<AlarmImageData> imageDatas;
    private int imageWidth;
    private GridViewAdapter adapter;

    public MessageGridView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public MessageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public MessageGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    /**
     * 更新数据
     *
     * @param datas
     */
    public void setDatas(List<AlarmImageData> datas) {
        if (datas == null) {
            datas = new ArrayList<AlarmImageData>();
        }
        this.imageDatas = datas;
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_message_grid, this);
        ViewHelper.inject(this, view);

//        initImageWidth();
        adapter = new GridViewAdapter();
        gridView.setAdapter(adapter);
    }

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imageDatas.size();
        }

        @Override
        public AlarmImageData getItem(int position) {
            return imageDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.alert_message_item, null);
                holder.alert_img = (ImageView) convertView.findViewById(R.id.alert_img);
                holder.select_img = (ImageView) convertView.findViewById(R.id.select_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AlarmImageData item = getItem(position);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.alert_img.getLayoutParams();
            params.width = 10;
            params.height = 10;
            Picasso.with(context).load(item.getImageUrl()).resize(imageWidth, imageWidth).centerCrop().into(holder.alert_img);


//            if (deleteList.contains(item.getId())) {
//                holder.select_img.setVisibility(View.VISIBLE);
//            } else {
//                holder.select_img.setVisibility(View.GONE);
//            }

            return convertView;
        }
    }

    public class ViewHolder {
        public ImageView alert_img;
        public ImageView select_img;
    }

    private void initImageWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels - Util.DensityUtil.dip2px(context, 8);
        imageWidth = width / 3;
    }
}
