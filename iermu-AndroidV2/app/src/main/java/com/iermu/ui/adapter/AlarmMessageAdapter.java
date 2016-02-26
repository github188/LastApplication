package com.iermu.ui.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.AlarmImageData;
import com.iermu.client.util.DateUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.MessageGridView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by zhoushaopei on 15/7/7.
 */
public class AlarmMessageAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, StickyListHeadersAdapter {
    private Activity context;
    private List<AlarmImageData> datas;
    private List<List<AlarmImageData>> datasArray;
    private List<Long> deleteList;
    private SelectDataChangeListener listener;
    private int imageWidth;
    private boolean isDeleting; // 记录是否处于删除状态
    private long headerId; // 记录headerId，每次+1

    private String todayStr;
    private String yesterdayStr;
    private String dayBeforeYesterdayStr;

    private void initImageWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels - Util.DensityUtil.dip2px(context, 16);
        imageWidth = width / 3;
    }

    public AlarmMessageAdapter(Activity ctx) {
        this.context = ctx;
        this.datas = new ArrayList<AlarmImageData>();
        this.datasArray = new ArrayList<List<AlarmImageData>>();
        deleteList = new ArrayList<Long>();
        initImageWidth();
        initDayInfo();
    }

    /**
     * 设置是否处于删除状态
     *
     * @param isDeleting
     */
    public void setIsDeleting(boolean isDeleting) {
        this.isDeleting = isDeleting;
    }

    public void setDatas(List<AlarmImageData> datas, List<List<AlarmImageData>> datasArray) {
        if (datas == null) {
            datas = new ArrayList<AlarmImageData>();
        }

        if (datasArray == null) {
            datasArray = new ArrayList<List<AlarmImageData>>();
        }
        this.datas = datas;
        this.datasArray = datasArray;
    }

    public void setListener(SelectDataChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return datasArray.size();
    }

    @Override
    public List<AlarmImageData> getItem(int position) {
        return datasArray.get(position);
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
            convertView = View.inflate(context, R.layout.alert_date_message_item, null);
            holder.gridView = (GridView) convertView.findViewById(R.id.gridView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        List<AlarmImageData> item = getItem(position);
        AlarmImageData imageData = item.get(0);

        // 动态计算gridView的高度
        int size = item.size();
        int colNum = size / 3;
        if (size % 3 > 0) {
            colNum++;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.gridView.getLayoutParams();
        params.height = imageWidth * colNum + Util.DensityUtil.dip2px(context, 4) * (colNum + 1);
        holder.gridView.setLayoutParams(params);
        GridViewAdapter adapter = new GridViewAdapter(item);
        holder.gridView.setAdapter(adapter);
        holder.gridView.setOnItemClickListener(this);
        holder.gridView.setTag(position);
        holder.gridView.setVerticalScrollBarEnabled(false);

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int parentPosition = (Integer) parent.getTag();
        AlarmImageData imageData = datasArray.get(parentPosition).get(position);
        // 获取被点击的图片对象在datas中的位置
        int imagePosition = datas.indexOf(imageData);

        if (isDeleting) {
            selectOrUnSelectOne(imagePosition);
        } else {
            if (listener != null) {
                listener.onImageClick(imagePosition);
            }
        }
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        ViewHeaderHolder holder = null;
        if (convertView == null) {
            holder = new ViewHeaderHolder();
            convertView = View.inflate(context, R.layout.alert_date_message_header_item, null);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHeaderHolder) convertView.getTag();
        }
        List<AlarmImageData> item = getItem(position);
        AlarmImageData imageData = item.get(0);

        // 处理父标题日期格式
        String dateStr = convertTime(imageData.getAlarmtime());
        holder.textViewDate.setText(dateStr);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return headerId++;
    }

    public class ViewHolder {
        public GridView gridView;
    }

    public class ViewHeaderHolder {
        public TextView textViewDate;
    }

    private class GridViewAdapter extends BaseAdapter {
        private List<AlarmImageData> imageDatas;

        public GridViewAdapter(List<AlarmImageData> imageDatas) {
            if (imageDatas == null) {
                imageDatas = new ArrayList<AlarmImageData>();
            }
            this.imageDatas = imageDatas;
        }

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
            ViewHolderGrid holder;
            if (convertView == null) {
                holder = new ViewHolderGrid();
                convertView = View.inflate(context, R.layout.alert_message_item, null);
                holder.alert_img = (ImageView) convertView.findViewById(R.id.alert_img);
                holder.select_img = (ImageView) convertView.findViewById(R.id.select_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderGrid) convertView.getTag();
            }
            AlarmImageData item = getItem(position);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.alert_img.getLayoutParams();
            params.width = imageWidth;
            params.height = imageWidth;
            Picasso.with(context).load(item.getImageUrl()).resize(imageWidth, imageWidth).centerCrop().into(holder.alert_img);

            if (deleteList.contains(item.getId())) {
                holder.select_img.setVisibility(View.VISIBLE);
            } else {
                holder.select_img.setVisibility(View.GONE);
            }

            return convertView;
        }

        private class ViewHolderGrid {
            public ImageView alert_img;
            public ImageView select_img;
        }
    }

    /**
     * 全选或全部取消
     */
    public void selectOrUnSelectAll() {
        if (deleteList.size() < datas.size()) {
            for (int i = 0; i < datas.size(); i++) {
                Long id = datas.get(i).getId();
                if (!deleteList.contains(id)) {
                    deleteList.add(id);
                }
            }
        } else {
            deleteList.clear();
        }

        notifyDataSetChanged();

        if (listener != null) {
            listener.onSelectDataChange(deleteList);
        }
    }

    /**
     * 选中或取消一个
     *
     * @param position
     */
    public void selectOrUnSelectOne(int position) {
        Long id = datas.get(position).getId();
        if (deleteList.contains(id)) {
            deleteList.remove(id);
        } else {
            deleteList.add(id);
        }

        notifyDataSetChanged();

        if (listener != null) {
            listener.onSelectDataChange(deleteList);
        }
    }

    /**
     * 清除选中状态
     */
    public void clearDeleteList() {
        deleteList.clear();
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectDataChange(deleteList);
        }
    }

    public List<Long> getDeleteList() {
        return deleteList;
    }

    public interface SelectDataChangeListener {
        void onSelectDataChange(List<Long> selectList);

        void onImageClick(int position);
    }

    private String convertTime(String oldTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
        String newTime = "";
        try {
            Date date = dateFormat.parse(oldTime);
            newTime = dateFormat1.format(date) + ":00";

            String dayNewTime = dateFormat3.format(date);
            if (dayNewTime.equals(todayStr)) {
                String newHour = dateFormat2.format(date);
                newTime = context.getString(R.string.today) + newHour + ":00";
            } else if (dayNewTime.equals(yesterdayStr)) {
                String newHour = dateFormat2.format(date);
                newTime = context.getString(R.string.yesterday) + newHour + ":00";
            } else if (dayNewTime.equals(dayBeforeYesterdayStr)) {
                String newHour = dateFormat2.format(date);
                newTime = context.getString(R.string.alarm_the_day_before_yesterday) + newHour + ":00";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }

    private void initDayInfo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long time = new Date().getTime();
        todayStr = dateFormat.format(new Date());
        yesterdayStr = dateFormat.format(new Date(time - DateUtil.DAY_SECOND_NUM * 1000l));
        dayBeforeYesterdayStr = dateFormat.format(new Date(time - DateUtil.DAY_SECOND_NUM * 2000l));
    }
}
