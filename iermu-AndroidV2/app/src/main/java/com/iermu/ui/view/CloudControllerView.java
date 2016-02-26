package com.iermu.ui.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.CloudPosition;
import com.iermu.ui.util.Util;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云台控制器
 * <p/>
 * Created by zhangxq on 15/10/19.
 */
public class CloudControllerView extends RelativeLayout implements ViewPager.OnPageChangeListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private Context context;

    @ViewInject(R.id.viewPagerCloud)
    ViewPager viewPagerCloud;
    @ViewInject(R.id.viewCloudBottom)
    View viewCloudBottom;
    @ViewInject(R.id.textViewPrepositionColor)
    TextView textViewPrepositionColor;
    @ViewInject(R.id.textViewAutoColor)
    TextView textViewAutoColor;
    @ViewInject(R.id.gridViewPosition)
    GridView gridViewPosition;
    @ViewInject(R.id.viewNoPrePosition)
    View viewNoPrePosition;
    @ViewInject(R.id.viewPositions)
    View viewPositions;

    private List<CloudPosition> positions = new ArrayList<CloudPosition>();
    private GridViewAdapter gridAdapter;
    private int windowWidth;

    private CloudPagerAdapter adapter;
    private boolean isAutoStart; // 记录是否打开了自动平扫
    private CloudCallBack listener;
    private boolean isShowPosition; // 记录是否打开了预置位面板

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setListener(CloudCallBack listener) {
        this.listener = listener;
    }

    /**
     * 更新预置位列表
     *
     * @param list
     */
    public void updatePositions(List<CloudPosition> list) {
        if (list == null) {
            this.positions = new ArrayList<CloudPosition>();
        } else {
            this.positions = list;
        }
        gridAdapter.notifyDataSetChanged();
        if (list.size() == 0) {
            viewNoPrePosition.setVisibility(View.VISIBLE);
        } else {
            viewNoPrePosition.setVisibility(View.GONE);
        }
    }

    /**
     * 预置位面板是否已显示
     *
     * @return
     */
    public boolean isShowPosition() {
        return isShowPosition;
    }

    /**
     * 隐藏预置位面板
     */
    public void hideGridViewPosition() {
        viewPositions.setVisibility(View.GONE);
        isShowPosition = false;
    }

    /**
     * 设置是否开启平扫
     *
     * @param isStartAuto
     */
    public void setIsStartAuto(boolean isStartAuto) {
        this.isAutoStart = isStartAuto;
        viewPagerCloud.setAdapter(adapter);
    }

    /**
     * 获取是否开启平扫
     *
     * @return
     */
    public boolean isAutoStart() {
        return isAutoStart;
    }

    /**
     * 刷新预置位图片
     */
    public void updatePositionsImage() {
        gridAdapter.notifyDataSetChanged();
    }

    public CloudControllerView(Context context) {
        super(context);
    }

    public CloudControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_cloud_controller, this);
        ViewHelper.inject(this, view);

        gridAdapter = new GridViewAdapter();
        gridViewPosition.setAdapter(gridAdapter);
        gridViewPosition.setOnItemClickListener(this);

        adapter = new CloudPagerAdapter();
        viewPagerCloud.setAdapter(adapter);
        viewPagerCloud.setOnPageChangeListener(this);

        textViewPrepositionColor.setOnClickListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            Animation animation = new TranslateAnimation(-Util.DensityUtil.dip2px(context, 35), Util.DensityUtil.dip2px(context, 35), 0, 0);
            animation.setDuration(300);
            animation.setFillAfter(true);
            viewCloudBottom.startAnimation(animation);
            textViewPrepositionColor.setTextColor(0xff00acef);
            textViewAutoColor.setTextColor(0xff888888);
        } else {
            Animation animation1 = new TranslateAnimation(Util.DensityUtil.dip2px(context, 35), -Util.DensityUtil.dip2px(context, 35), 0, 0);
            animation1.setDuration(300);
            animation1.setFillAfter(true);
            viewCloudBottom.startAnimation(animation1);
            textViewPrepositionColor.setTextColor(0xff888888);
            textViewAutoColor.setTextColor(0xff00acef);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        windowWidth = r - l;
        super.onLayout(changed, l, t, r, b);
    }

    @OnClick(value = {R.id.textViewAutoColor, R.id.textViewPrepositionColor})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewAutoColor:
                textViewPrepositionColor.setEnabled(true);
                textViewAutoColor.setEnabled(false);
                viewPagerCloud.setCurrentItem(1);
                Animation animation1 = new TranslateAnimation(Util.DensityUtil.dip2px(context, 35), -Util.DensityUtil.dip2px(context, 35), 0, 0);
                animation1.setFillAfter(true);
                viewCloudBottom.startAnimation(animation1);
                textViewPrepositionColor.setTextColor(0xff888888);
                textViewAutoColor.setTextColor(0xff00acef);
                break;
            case R.id.textViewPrepositionColor:
                textViewPrepositionColor.setEnabled(false);
                textViewAutoColor.setEnabled(true);
                viewPagerCloud.setCurrentItem(0);
                Animation animation = new TranslateAnimation(-Util.DensityUtil.dip2px(context, 35), Util.DensityUtil.dip2px(context, 35), 0, 0);
                animation.setFillAfter(true);
                viewCloudBottom.startAnimation(animation);
                textViewPrepositionColor.setTextColor(0xff00acef);
                textViewAutoColor.setTextColor(0xff888888);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onPositionClick(position);
        }
        gridAdapter.setSelectedPosition(position);
        gridAdapter.notifyDataSetChanged();
    }

    /**
     * 滑动页面适配器
     */
    private class CloudPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        //滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        //每次滑动的时候生成的组件
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(context, R.layout.view_cloud_pager, null);
            container.addView(view);

            final TextView textView = (TextView) view.findViewById(R.id.textViewCloudPager);
            final ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButtonCloudPager);
            if (position == 0) {
                textView.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);
                textView.setText(R.string.check_use_location);
                imageButton.setImageResource(R.drawable.cloud_btn_preposition);
                imageButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPositions.setVisibility(View.VISIBLE);
                        isShowPosition = true;
                        if (listener != null) {
                            listener.onPrePositionClick();
                        }
                    }
                });
                Animation animation1 = new TranslateAnimation(0, Util.DensityUtil.dip2px(context, 35), 0, 0);
                animation1.setFillAfter(true);
                viewCloudBottom.startAnimation(animation1);
            } else {
                if (!isAutoStart) {
                    textView.setText(R.string.start_switch);
                    imageButton.setImageResource(R.drawable.cloud_auto_start);
                } else {
                    textView.setText(R.string.stop_switch);
                    imageButton.setImageResource(R.drawable.cloud_auto_stop);
                }
                imageButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isAutoStart) {
                            imageButton.setImageResource(R.drawable.cloud_auto_start);
                            textView.setText(R.string.start_switch);
                            if (listener != null) {
                                listener.onStopAutoClick();
                            }
                        } else {
                            imageButton.setImageResource(R.drawable.cloud_auto_stop);
                            textView.setText(R.string.stop_switch);
                            if (listener != null) {
                                listener.onStartAutoClick();
                            }
                        }
                        isAutoStart = !isAutoStart;
                    }
                });
            }
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * 预置位列表适配器
     */
    private class GridViewAdapter extends BaseAdapter {
        private int selectedPosition = -1;

        public void setSelectedPosition(int selectPosition) {
            this.selectedPosition = selectPosition;
        }

        @Override
        public int getCount() {
            return positions.size();
        }

        @Override
        public CloudPosition getItem(int position) {
            return positions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(context, R.layout.item_cloud_position, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewPosition);
                holder.textView = (TextView) convertView.findViewById(R.id.textViewPositionTitle);
                holder.imageViewEdit = (ImageView) convertView.findViewById(R.id.imageButtonPositionEdit);
                holder.viewMask = convertView.findViewById(R.id.viewPositionMask);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            if (position == selectedPosition) {
                holder.viewMask.setBackgroundResource(R.drawable.cloud_position_item_mask_pressed);
            } else {
                holder.viewMask.setBackgroundResource(R.drawable.cloud_position_item_mask_normal);
            }

            int imageWidth = (windowWidth - Util.DensityUtil.dip2px(context, 8) * 3) / 2;
            int imageHeight = imageWidth * 9 / 16;
            int maskHeight = imageHeight + Util.DensityUtil.dip2px(context, 35);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.imageView.getLayoutParams();
            layoutParams.width = imageWidth;
            layoutParams.height = imageHeight;
            holder.imageView.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams layoutParams1 = (LayoutParams) holder.viewMask.getLayoutParams();
            layoutParams1.width = imageWidth;
            layoutParams1.height = maskHeight;
            holder.viewMask.setLayoutParams(layoutParams1);

            CloudPosition cloudPosition = getItem(position);
            holder.textView.setText(cloudPosition.getTitle());
            String imagePath = cloudPosition.getImagePath();
            if (imagePath == null || imagePath.length() == 0) {
                imagePath = "123";
            }
            Picasso.with(context).load(new File(imagePath)).resize(imageWidth, imageHeight).centerCrop().into(holder.imageView);
            holder.imageViewEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPositionEditClick(position);
                    }
                }
            });

            return convertView;
        }

        private class Holder {
            public ImageView imageView;
            public TextView textView;
            public ImageView imageViewEdit;
            public View viewMask;
        }
    }

    public interface CloudCallBack {
        /**
         * 点击开始自动旋转按钮
         */
        void onStartAutoClick();

        /**
         * 点击结束自动旋转按钮
         */
        void onStopAutoClick();

        /**
         * 点击查看预置位按钮
         */
        void onPrePositionClick();

        /**
         * 预置位编辑按钮点击
         *
         * @param position
         */
        void onPositionEditClick(int position);

        /**
         * 预置位点击
         *
         * @param position
         */
        void onPositionClick(int position);
    }
}
