package com.iermu.ui.fragment.message;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.business.dao.AlarmImageDataWrapper;
import com.iermu.client.model.AlarmImageData;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.adapter.BigImageViewPagerAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonDialog;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * Created by zhoushaopei on 15/7/10.
 */
public class AlarmBigImageFragment extends BaseFragment implements ViewPager.OnPageChangeListener, BigImageViewPagerAdapter.OnPageChangeListener {
    public static final String INTENT_DELETE_IMAGEDATA = "intentDeleteImageData";

    private static final String KEY_DEVICE_NAME = "deviceName";
    private static final String KEY_SELECT_POSITION = "selectPosition";
    private static final String KEY_IMAGE_DATAS = "imageDatas";

    @ViewInject(R.id.textViewTitle)
    TextView textViewTitle;
    @ViewInject(R.id.textViewTime)
    TextView textViewTime;
    @ViewInject(R.id.deleteButton)
    ImageButton deleteButton;
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;
    @ViewInject(R.id.textViewNum)
    TextView textViewNum;

    private List<AlarmImageData> datas = new ArrayList<AlarmImageData>();
    private BigImageViewPagerAdapter adapter;
    private int currentItem;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_single_img);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        deleteButton.setVisibility(View.VISIBLE);
    }

    private static boolean isOpened;
    public static Fragment actionInstance(String deviceName, List<AlarmImageData> imageDatas, int selectPosition) {
        if (!isOpened) {
            isOpened = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOpened = false;
                }
            }, 1000);

            AlarmBigImageFragment fragment = new AlarmBigImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_DEVICE_NAME, deviceName);
            bundle.putInt(KEY_SELECT_POSITION, selectPosition);
            bundle.putSerializable(KEY_IMAGE_DATAS, (Serializable) imageDatas);
            fragment.setArguments(bundle);
            return fragment;
        } else {
            return null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_alarm_big_image, null);
        ViewHelper.inject(this, view);

        String deviceName = getArguments().getString(KEY_DEVICE_NAME);
        List<AlarmImageData> imageDatas = (List<AlarmImageData>) getArguments().getSerializable(KEY_IMAGE_DATAS);
        datas = new ArrayList<AlarmImageData>(imageDatas);
        int selectPosition = getArguments().getInt(KEY_SELECT_POSITION);

        adapter = new BigImageViewPagerAdapter(getActivity(), datas);
        viewPager.setOnPageChangeListener(this);
        adapter.setListener(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectPosition);
        AlarmImageData imageData = datas.get(selectPosition);
        Logger.e("选择的图片:" + imageData.getImageUrl());
        textViewNum.setText("1/" + datas.size());
        textViewTitle.setText(deviceName);

        return view;
    }

    @OnClick(value = {R.id.actionbar_back, R.id.deleteButton})
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.actionbar_back:
                popBackStack();
                break;
            case R.id.deleteButton:
                final CommonDialog commonDialog = new CommonDialog(getActivity());
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog.setTitle(getString(R.string.delete_photo_sure))
                        .setContent(getString(R.string.delete_photo_content))
                        .setCancelText(getString(R.string.cancle_txt))
                        .setOkText(getString(R.string.sure))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentItem = viewPager.getCurrentItem();
                                AlarmImageData data = datas.get(currentItem);
                                AlarmImageDataWrapper.deleteById(data.getId());
                                datas.remove(currentItem);
                                Intent intent = new Intent();
                                intent.setAction(INTENT_DELETE_IMAGEDATA);
                                intent.putExtra("deleteIndex", currentItem);
                                getActivity().sendBroadcast(intent);
                                viewPager.setAdapter(adapter);
                                viewPager.setCurrentItem(currentItem);
                                textViewNum.setText((currentItem + 1) + "/" + datas.size());
                                if (currentItem + 1 == 1) {
                                    if (datas.size() >= 1) {
                                        viewPager.setCurrentItem(currentItem);
                                    } else {
                                        popBackStack();
                                    }

                                }
                                commonDialog.dismiss();
                            }
                        })
                        .setCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonDialog.dismiss();
                            }
                        }).show();
                break;
        }
    }


    @Override
    public void onPageChanged() {
        int position = viewPager.getCurrentItem();
        textViewNum.setText((position + 1) + "/" + datas.size());
        String newTime = convertTime(datas.get(position).getAlarmtime());
        textViewTime.setText(newTime);
        viewPager.getCurrentItem();
    }

    private String convertTime(String oldTime) {
        String language = LanguageUtil.getLanguage();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(language.equals("zh") ? "MM月dd号 HH:mm" : "HH:mm MM/dd ");
        String newTime = "";
        try {
            Date date = dateFormat.parse(oldTime);
            newTime = dateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {
        int position = viewPager.getCurrentItem();
        textViewNum.setText((position + 1) + "/" + datas.size());
        String newTime = convertTime(datas.get(position).getAlarmtime());
        textViewTime.setText(newTime);
        viewPager.getCurrentItem();
    }
}
