package com.iermu.ui.fragment.MineIermu;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IMineRecordBusiness;
import com.iermu.client.business.dao.CamSettingDataWrapper;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnCardInfoChangeListener;
import com.iermu.client.listener.OnGetNasParamListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.lan.model.CardInforResult;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.model.NasParamResult;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;

import java.util.Map;

/**
 * Created by zhangxq on 15/12/3.
 */
public class MainRecordFragment extends BaseFragment implements OnCardInfoChangeListener,
        MineCardRecordFragment.IKnowCallBack, OnCamSettingListener, OnGetNasParamListener {

    private static final String KEY_DEVICEID = "deviceId";
    private static final String KEY_IS_LANSCAPE = "isLanscape";

    private String deviceId;

    private boolean isInLocalNet = false; // 记录手机和摄像头是否是同一局域网
    private boolean isFinishShow = false; // 记录右上角按钮是否显示
    private CardInforResult result; // 卡录信息

    private MineRecordFragment fragmentRecord;
    private MineCardRecordFragment fragmentCard;
    private MineNasRecordFragment fragmentNas;
    private FragmentManager fragmentManager;
    private boolean isCard = false; // 记录当前是否是卡录页面

    private IMineRecordBusiness business;
    private ICamSettingBusiness settingBusiness;
    private int findCardInfoNum; // 卡录信息获取重试次数 不大于20次

    private boolean isNas;//true:nas;false:card

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.cloud_record)
                .setCommonFinish(R.string.local_record)
                .setCommonFinishClick(new OnCommonClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isInLocalNet) {
                            if (!isCard) {
                                fragmentRecord.toast(getString(R.string.record_same_lan));
                                business.findCardInfo(deviceId);
                            }
                        } else {
                            switchFragment();
                        }
                    }
                }).setCommonBackClick(new OnCommonClickListener() {
            @Override
            public void onClick(View v) {
                popBackAllStack();
            }
        });

        setCommonFinishHided();
        setCommonFinishDisabled();
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    /**
     * 带横竖屏切换，打开录像页面
     *
     * @param deviceId
     * @return
     */
    public static Fragment actionInstance(String deviceId) {
        return actionInstance(deviceId, false);
    }

    public static Fragment actionInstance(String deviceId, boolean isLanscape) {
        MainRecordFragment fragment = new MainRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEVICEID, deviceId);
        bundle.putBoolean(KEY_IS_LANSCAPE, isLanscape);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_main_record, null);
        ViewHelper.inject(this, view);

        // 初始化参数
        Bundle bundle = getArguments();
        deviceId = bundle.get(KEY_DEVICEID).toString();
        Boolean isLanscape = bundle.getBoolean(KEY_IS_LANSCAPE);

        fragmentRecord = (MineRecordFragment) MineRecordFragment.actionInstance(deviceId, isLanscape);
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentRecord);
        transaction.commit();

        business = ErmuBusiness.getMineRecordBusiness();
        business.registerListener(OnCardInfoChangeListener.class, this);
        business.registerListener(OnGetNasParamListener.class, this);
        settingBusiness = ErmuBusiness.getCamSettingBusiness();

        checkIsInLocalNet();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!isCard) {
                setCommonTitle(getString(R.string.cloud_record));
                if (isFinishShow) {
                    setCommonFinish(getString(R.string.local_record));
                    setCommonFinishEnbled();
                }
            } else {
                setCommonTitle(getString(R.string.local_record));
                setCommonFinish(getString(R.string.cloud_record));
                setCommonFinishEnbled();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!existBackStackTop()) {
            return;
        }
        if (isCard) {
            if (isNas) {
                if (fragmentNas.isAdded()) {
                    fragmentNas.onFragmentResume();
                }
            } else {
                if (fragmentCard.isAdded()) {
                    fragmentCard.onFragmentResume();
                }
            }
        } else {
            if (fragmentRecord.isAdded()) {
                fragmentRecord.onFragmentResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!existBackStackTop()) {
            return;
        }
        if (isCard) {
            if (isNas) {
                if (fragmentNas.isAdded()) {
                    fragmentNas.onFragmentPause();
                }
            } else {
                if (fragmentCard.isAdded()) {
                    fragmentCard.onFragmentPause();
                }
            }
        } else {
            if (fragmentRecord.isAdded()) fragmentRecord.onFragmentPause();
        }
    }

    /**
     * 查看手机和摄像机是否在同一局域网
     */
    private void checkIsInLocalNet() {
        // 初始化本地录像按钮
        String uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        CamInfo camInfo = CamSettingDataWrapper.getCamSettingInfo(uid, deviceId);
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        Logger.d("camInfo:" + camInfo);
        if (camInfo == null) {
            settingBusiness.registerListener(OnCamSettingListener.class, this);
            settingBusiness.syncCamInfo(deviceId);
        } else {
            String platform = camInfo.getPlatform();
            //小球、商铺摄像机
            if ("100".equals(platform) || "9".equals(platform)) {
                isNas = false;
                setCommonFinishShow();
                isFinishShow = true;
                business.findCardInfo(deviceId);
            } else {
                business.getNasParam(ErmuApplication.getContext(), deviceId, ErmuBusiness.getAccountAuthBusiness().getBaiduUid());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.i("IErmuSDK.java", "MainRecordFragment >>>>>>> onDestroyView");
        business.unRegisterListener(OnCardInfoChangeListener.class, this);
        settingBusiness.unRegisterListener(OnCamSettingListener.class, this);
        settingBusiness.unRegisterListener(OnGetNasParamListener.class, this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        if (isCard) {
            if (isNas) {
                fragmentNas.onDestroyView();
            } else {
                fragmentCard.onDestroyView();
            }
        } else {
            fragmentRecord.onDestroyView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isCard) {
                if (isNas) {
                    if (fragmentNas.hideSelectDate()) {
                        return true;
                    }

                } else {
                    if (fragmentCard.hideSelectDate()) {
                        return true;
                    }
                }
            } else {
                if (fragmentRecord.hideSelectDate()) {
                    return true;
                }
            }
            popBackAllStack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCardInfoChange(ErrorCode errorCode) {
        if (getActivity() != null) {
            findCardInfoNum++;
            Logger.d("findCardInfoNum:" + findCardInfoNum);
            if (errorCode.getIndex() == ErrorCode.SUCCESS.ordinal()) {
                result = business.getCardInfo(deviceId);
                if (result != null) {
                    isInLocalNet = true;
                    Logger.d("isInLocalNet" + isInLocalNet);
                }
                setCommonFinishEnbled();
            } else {
                if (findCardInfoNum > 3) {
                    setCommonFinishEnbled();
                } else {
                    business.findCardInfo(deviceId);
                }
            }
        }
    }

    @Override
    public void OnGetNasParamListener(NasParamResult result) {
        Map map = result.getMap();
        if (map != null && !"".equals(map.get("ip"))) {
            isNas = true;
            isInLocalNet = true;
            isFinishShow = true;
            setCommonFinishShow();
            setCommonFinishEnbled();
        }
    }

    @Override
    public void onIKnowClick() {
        switchFragment();
    }

    private synchronized void switchFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (isCard) {
            transaction.replace(R.id.frameLayout, fragmentRecord);
            setCommonTitle(getString(R.string.cloud_record));
            setCommonFinish(getString(R.string.local_record));
        } else {
            if (isNas) {
                fragmentNas = (MineNasRecordFragment) MineNasRecordFragment.actionInstance(deviceId);
                transaction.replace(R.id.frameLayout, fragmentNas);
            } else {
                fragmentCard = (MineCardRecordFragment) MineCardRecordFragment.actionInstance(deviceId, result);
                fragmentCard.setIknowListener(MainRecordFragment.this);
                transaction.replace(R.id.frameLayout, fragmentCard);
            }
            setCommonTitle(getString(R.string.local_record));
            setCommonFinish(getString(R.string.cloud_record));
        }
        transaction.commit();
        isCard = !isCard;
        diableFinishTime(100);
    }

    /**
     * 禁用finish按钮delayTime毫秒
     *
     * @param delayTime
     */
    private void diableFinishTime(int delayTime) {
        setCommonFinishDisabled();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setCommonFinishEnbled();
            }
        }, delayTime);
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business business) {
        if (type == CamSettingType.INFO && business.isSuccess()) {
            checkIsInLocalNet();
        }
    }
}
