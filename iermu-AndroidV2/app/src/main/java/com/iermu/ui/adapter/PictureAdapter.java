package com.iermu.ui.adapter;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.model.ScreenClip;
import com.iermu.client.model.constant.PhotoType;
import com.iermu.client.util.DateUtil;
import com.iermu.ui.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by zhoushaopei on 16/1/5.
 */
public class PictureAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Activity context;
    private List<ScreenClip> mList;
    private Map<String, ScreenClip> map;
    private int imageWidth;
    private boolean isShowTime = false;
    private String baiduAK;
    private SelectDataChangeListener listener;

    public PictureAdapter(FragmentActivity activity) {
        this.mList = new ArrayList<ScreenClip>();
        this.map = new HashMap<String, ScreenClip>();
        this.context = activity;
        this.baiduAK = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
        initImageWidth();
    }

    public void setChangeListener(SelectDataChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ScreenClip getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.photo_item_view, null);
            convertView.findViewById(R.id.image_list);

            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            holder.mTimeTxt = (TextView) convertView.findViewById(R.id.time_txt);
            holder.mIsSelectPic = (ImageView) convertView.findViewById(R.id.is_select);
            holder.mClipPlay = (ImageView) convertView.findViewById(R.id.clip_play);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScreenClip item = getItem(position);
        String path = item.getPath();
        long time = item.getTime();

        ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
        params.width = imageWidth;
        params.height = imageWidth * 9 / 16;
        holder.imageView.setLayoutParams(params);
        holder.mTimeTxt.setVisibility(isShowTime == true ? View.INVISIBLE : View.VISIBLE);
        holder.mIsSelectPic.setVisibility(isShowTime == true ? View.VISIBLE : View.INVISIBLE);

        if (map.containsKey(path)) {
            holder.mIsSelectPic.setBackgroundResource(R.drawable.add_icon_finish);
        } else {
            holder.mIsSelectPic.setBackgroundResource(R.drawable.add_icon_normal);
        }
        int type = item.getType();
        if (type == PhotoType.PHOTO) {
            holder.mClipPlay.setVisibility(View.GONE);
            String hourAndMin = DateUtil.getHourAndMin(time);
            holder.mTimeTxt.setText(hourAndMin);
            Picasso.with(context).load(new File(path)).into(holder.imageView);
        } else {
            holder.mClipPlay.setVisibility(View.VISIBLE);
            String hourAndMin = DateUtil.getHourAndMin(time);
            holder.mTimeTxt.setText(hourAndMin);

            String url = ApiConfig.getDeleteClip(baiduAK, path);
            Picasso.with(context).load(url).into(holder.imageView);
        }
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup viewGroup) {
        ViewHeaderHolder holder = null;
        if (convertView == null) {
            holder = new ViewHeaderHolder();
            convertView = View.inflate(context, R.layout.picture_header_item, null);
            holder.mTimeHead = (TextView) convertView.findViewById(R.id.time_head);
            convertView.setTag(holder);
        } else {
            holder = (ViewHeaderHolder) convertView.getTag();
        }
        long time = getItem(position).getTime();
        SimpleDateFormat df = new SimpleDateFormat(context.getResources().getString(R.string.SHORT_DATE_FORMAT3));
        String yearMothDay = df.format(new Date(time));
        holder.mTimeHead.setText(yearMothDay);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        long time = getItem(position).getTime();
        String yearMothDay = DateUtil.getPicTime(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(yearMothDay);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public void setTimeShow() {
        map.clear();
        isShowTime = false;
        notifyDataSetChanged();
    }


    public void setTimeHide() {
        map.clear();
        isShowTime = true;
        notifyDataSetChanged();
    }

    //取消或者选中一个
    public void selectOrUnSelectOne(int position) {
        ScreenClip screener = mList.get(position);
        String path = screener.getPath();
        if (!map.containsKey(path)) {
            map.put(path, screener);
        } else {
            map.remove(path);
        }
       notifyDataSetChanged();
       if (listener != null) {
           listener.onSelectDataChange(map);
       }
    }

    //全部选中或全部非选中
    public void selectAllOrUnSelectAll() {
        if (map.size() < mList.size()) {
            for (int i = 0; i < mList.size(); i++) {
                ScreenClip picture = mList.get(i);
                String path = picture.getPath();
                if (!map.containsKey(path)) {
                    map.put(path, picture);
                }
            }
        } else {
            map.clear();
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectDataChange(map);
        }
    }

    //清除选中状态
    public void clearDeleteList() {
        map.clear();
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectDataChange(map);
        }
    }

    public Map<String, ScreenClip> getDeleteList() {
        return map;
    }

    public class ViewHeaderHolder {
        public TextView mTimeHead;
    }

    class ViewHolder{
        ImageView imageView;
        TextView  mTimeTxt;
        ImageView mIsSelectPic;
        ImageView mClipPlay;
    }

    public interface SelectDataChangeListener {

        void onSelectDataChange(Map<String, ScreenClip> map);

        void onClickImage(int position);
    }

    private void initImageWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels - Util.DensityUtil.dip2px(context, 78);
        imageWidth = width;
    }

    public void notifyDataSetChanged(List<ScreenClip> list) {
        if (list == null) {
            list = new ArrayList<ScreenClip>();
        }
        this.mList = list;
        notifyDataSetChanged();
    }
}
