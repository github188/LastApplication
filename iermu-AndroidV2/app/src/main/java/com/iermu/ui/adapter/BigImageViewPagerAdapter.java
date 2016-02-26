package com.iermu.ui.adapter;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iermu.R;
import com.iermu.client.model.AlarmImageData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by zhangxq on 15/8/21.
 */
public class BigImageViewPagerAdapter extends PagerAdapter {
    private List<AlarmImageData> imageDatas = new ArrayList<AlarmImageData>();
    private Activity context;
    private int screenWidth;

    private OnPageChangeListener listener;
//    private OnLongClickListener mListener;

    public BigImageViewPagerAdapter(Activity context, List<AlarmImageData> imageDatas) {
        this.context = context;
        if (imageDatas != null) {
            this.imageDatas = imageDatas;
        }
        initImageWidth();
    }

    private void initImageWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
    }

    public void setListener(OnPageChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return imageDatas.size();
    }

    //滑动切换的时候销毁当前的组件
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //每次滑动的时候生成的组件
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.view_big_image_item, null);
        container.addView(view);

        final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
        final View viewBg = view.findViewById(R.id.viewBg);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewBg.getLayoutParams();
        params.width = screenWidth;
        params.height = screenWidth * 9 / 16;
        viewBg.setLayoutParams(params);

        final View imageViewLoading = view.findViewById(R.id.viewLoading);

        AlarmImageData data = imageDatas.get(position);
        Picasso.with(context).load(data.getImageUrl())
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageViewLoading.setVisibility(View.GONE);
                        viewBg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        if (listener != null) {
            listener.onPageChanged();
        }

        return view == object;
    }


    public interface OnPageChangeListener {
        // 页面切换时调用
        void onPageChanged();
    }
}
