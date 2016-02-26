package com.iermu.ui.adapter;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.CamThumbnail;
import com.iermu.client.util.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordListAdapter extends BaseAdapter {
    public static final int TYPE_FRAGMENT = 1;
    public static final int TYPE_CONTROLLER = 2;

    private FragmentActivity context;
    private List<CamThumbnail> thumbnails = new ArrayList<CamThumbnail>();
    private int adapterType = TYPE_FRAGMENT;
    private Map<Integer, Holder> viewMap;

    private Holder selectedHolder;
    private int selectedPosition;

    public RecordListAdapter(FragmentActivity context) {
        this.context = context;
        this.thumbnails = new ArrayList<CamThumbnail>();
        viewMap = new HashMap<Integer, Holder>();
    }

    @Override
    public int getCount() {
        return thumbnails.size();
    }

    public void setThumbnails(List<CamThumbnail> list) {
        if (list == null) {
            thumbnails = new ArrayList<CamThumbnail>();
        } else {
            thumbnails = list;
        }
    }

    public void setAdapterType(int type) {
        this.adapterType = type;
    }

    public void setSelectedPosition(int selectPosition) {
        if (selectedHolder != null) {
            selectedHolder.imageViewMask.setBackgroundResource(R.drawable.mine_record_list_item_bg_normal);
            selectedHolder.viewBg.setBackgroundResource(R.drawable.record_live_ctrl_list_item_bg_normal);
        }

        this.selectedPosition = selectPosition;
        this.selectedHolder = viewMap.get(selectPosition);

        if (selectedHolder != null) {
            selectedHolder.imageViewMask.setBackgroundResource(R.drawable.mine_record_list_item_bg_selected);
            selectedHolder.viewBg.setBackgroundResource(R.drawable.record_live_ctrl_list_item_bg_selected);
        }
    }

    @Override
    public CamThumbnail getItem(int position) {
        return thumbnails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View viewFrag = null;
        View viewCtrl = null;
        if (convertView != null && (Integer) convertView.getTag() == TYPE_FRAGMENT) {
            viewFrag = convertView;
        } else {
            viewFrag = View.inflate(context, R.layout.item_record_live_list, null);
        }

        if (convertView != null && (Integer) convertView.getTag() == TYPE_CONTROLLER) {
            viewCtrl = convertView;
        } else {
            viewCtrl = View.inflate(context, R.layout.item_record_live_list_ctrl, null);
        }

        holder.imageViewMask = (ImageView) viewFrag.findViewById(R.id.imageViewMask);
        holder.imageViewFrag = (ImageView) viewFrag.findViewById(R.id.imageView);
        holder.textViewFrag = (TextView) viewFrag.findViewById(R.id.textView);

        holder.viewBg = viewCtrl.findViewById(R.id.viewBg);
        holder.imageViewCtrl = (ImageView) viewCtrl.findViewById(R.id.imageView);
        holder.textViewCtrl = (TextView) viewCtrl.findViewById(R.id.textView);

        // 记录holder到map，用来做选中和取消选中
        viewMap.put(position, holder);

        if (position == selectedPosition) {
            selectedHolder = holder;
            holder.imageViewMask.setBackgroundResource(R.drawable.mine_record_list_item_bg_selected);
            holder.viewBg.setBackgroundResource(R.drawable.record_live_ctrl_list_item_bg_selected);
        } else {
            holder.imageViewMask.setBackgroundResource(R.drawable.mine_record_list_item_bg_normal);
            holder.viewBg.setBackgroundResource(R.drawable.record_live_ctrl_list_item_bg_normal);
        }

        CamThumbnail item = getItem(position);
        Date date = new Date(item.getTime() * 1000l);
        String dateStr = DateUtil.formatDate(date, DateUtil.FORMAT_ONE);
        if (adapterType == TYPE_FRAGMENT) {
            holder.textViewFrag.setText(dateStr);
            if (!TextUtils.isEmpty(item.getUrl())) {
                Picasso.with(context)
                        .load(item.getUrl())
                        .into(holder.imageViewFrag);
            }
            viewFrag.setTag(adapterType);
            return viewFrag;
        } else {
            holder.textViewCtrl.setText(dateStr);
            if (!TextUtils.isEmpty(item.getUrl())) {
                Picasso.with(context)
                        .load(item.getUrl())
                        .into(holder.imageViewCtrl);
            }
            viewCtrl.setTag(adapterType);
            return viewCtrl;
        }
    }

    /**
     * View holder for the views we need access to
     */
    private static class Holder {
        public ImageView imageViewFrag;
        public TextView textViewFrag;
        public ImageView imageViewMask;

        public ImageView imageViewCtrl;
        public TextView textViewCtrl;
        public View viewBg;
    }
}
