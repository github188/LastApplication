package com.iermu.ui.fragment.personalinfo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.model.ScreenClip;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.FileUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.adapter.ScreenFilmAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.CommonDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by zhoushaopei on 15/7/10.
 */
public class ScreenFilmFragment extends BaseFragment implements ViewPager.OnPageChangeListener, ScreenFilmAdapter.OnPageChangeListener, PlatformActionListener, View.OnTouchListener {

    public static final String INTENT_DELETE_IMAGEDATA = "intentDeleteImageData";
    private static final String KEY_DEVICE_NAME = "deviceName";
    private static final String KEY_SELECT_POSITION = "selectPosition";
    private static final String KEY_IMAGE_DATAS = "imageDatas";

    @ViewInject(R.id.textViewTitle) TextView textViewTitle;
    @ViewInject(R.id.textViewTime)  TextView textViewTime;
    @ViewInject(R.id.deleteButton)  ImageButton deleteButton;
    @ViewInject(R.id.viewPager)     ViewPager viewPager;
    @ViewInject(R.id.textViewNum)   TextView textViewNum;
    @ViewInject(R.id.share_friends) Button mShare;
    @ViewInject(R.id.share_trans)   View mShareTrans;
    @ViewInject(R.id.share_wechat)  Button mShareWx;
    @ViewInject(R.id.share_moments) Button mShareMoments;
    @ViewInject(R.id.share_type)    RelativeLayout mShareType;

    private List<ScreenClip> datas = new ArrayList<ScreenClip>();
    private ScreenFilmAdapter adapter;
    private int currentItem;
    private boolean isShareVisible = false;
    private OnDataChangeListener listener;
    private static boolean isOpened;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_single_img);
    }

    public void setDataChangeListener(OnDataChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        deleteButton.setVisibility(View.VISIBLE);
    }

    public static ScreenFilmFragment actionInstance(String deviceName, List<ScreenClip> imageDatas, int selectPosition) {
        if (!isOpened) {
            isOpened = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOpened = false;
                }
            }, 1000);
            ScreenFilmFragment fragment = new ScreenFilmFragment();
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
        View view = View.inflate(getActivity(), R.layout.fragment_screener_film_image, null);
        ViewHelper.inject(this, view);

        initViewPager();
        return view;
    }

    private void initViewPager() {
        mShareTrans.setOnTouchListener(this);
        Bundle bundle = getArguments();
        String deviceName = bundle.getString(KEY_DEVICE_NAME);
        datas = (List<ScreenClip>) bundle.getSerializable(KEY_IMAGE_DATAS);
        int selectPosition = bundle.getInt(KEY_SELECT_POSITION);

        adapter = new ScreenFilmAdapter(getActivity(), datas);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
        adapter.setListener(this);
        viewPager.setCurrentItem(selectPosition);
        ScreenClip imageData = datas.get(selectPosition);
        Logger.e("选择的图片:" + imageData.getPath());
        textViewNum.setText("1/" + datas.size());
        textViewTitle.setText(deviceName);
    }

    @OnClick(value = {R.id.actionbar_back, R.id.deleteButton, R.id.share_friends, R.id.share_wechat, R.id.share_moments, R.id.share_cancel})
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
                                ScreenClip data = datas.get(currentItem);
                                String path = data.getPath();
                                FileUtil.deleteFile(path);
                                datas.remove(data);
                                adapter.notifyDataChange(datas);
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
            case R.id.share_friends:
                aniIn();
                break;
            case R.id.share_wechat:
                mShareWx.setClickable(false);
                Platform.ShareParams wechat = shareParams();
                Platform weixin = ShareSDK.getPlatform(getActivity(), Wechat.NAME);
                weixin.setPlatformActionListener(this);
                weixin.share(wechat);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mShareWx.setClickable(true);
                    }
                }, 300);
                break;
            case R.id.share_moments:
                mShareMoments.setClickable(false);
                Platform.ShareParams wechatMoments = shareParams();
                Platform wXP = ShareSDK.getPlatform(getActivity(), WechatMoments.NAME);
                wXP.setPlatformActionListener(this);
                wXP.share(wechatMoments);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mShareMoments.setClickable(true);
                    }
                }, 300);
                break;
            case R.id.share_cancel:
                animationOut();
                break;
        }
    }

    private Platform.ShareParams shareParams() {
        int item = viewPager.getCurrentItem();
        ScreenClip picture = datas.get(item);
        String path = picture.getPath();

        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setImagePath(path);
        wechat.setShareType(Platform.SHARE_IMAGE);
        return wechat;
    }

    private void aniIn() {
        mShare.setVisibility(View.GONE);
        isShareVisible = true;
        mShareType.setVisibility(View.VISIBLE);
        mShareTrans.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp).duration(300).playOn(mShareType);
    }

    private void animationOut() {
        mShare.setVisibility(View.VISIBLE);
        isShareVisible = false;
        mShareTrans.setVisibility(View.GONE);
        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(mShareType);
    }

    private void initItem() {
        int item = viewPager.getCurrentItem();
        ScreenClip picture = datas.get(item);
        long time = picture.getTime();
        String titleTime = DateUtil.getTitleTime(time);
        textViewNum.setText((item + 1) + "/" + datas.size());
        textViewTime.setText(titleTime);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {
        initItem();
    }

    @Override
    public void onPageChanged() {
        initItem();
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> stringObjectHashMap) {

    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {

    }

    @Override
    public void onCancel(Platform platform, int action) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.share_trans) {
            int y = (int) event.getY();
            int top = mShareType.getTop();
            if (y < top && isShareVisible) animationOut();
            return false;
        }
        return false;
    }

    public interface OnDataChangeListener {
        // 页面切换时调用
        void onDataChanged();
    }
}
