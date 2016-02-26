package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.business.dao.CamSettingDataWrapper;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnCheckCamFirmwareListener;
import com.iermu.client.listener.OnDropCamListener;
import com.iermu.client.listener.OnPowerCamListener;
import com.iermu.client.listener.OnRestartCamListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.Connect;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.CronType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.MineIermu.RecordInfoFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.CommonDialog;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * Created by xjy on 15/6/25.
 */
public class SettingFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, OnCamSettingListener, OnPowerCamListener,
        OnRestartCamListener, OnDropCamListener, EditCamNameFragment.OnDevNameChange, OnCheckCamFirmwareListener {


    @ViewInject(R.id.set_camera_name)
    TextView setCameraName;
    @ViewInject(R.id.campower_btn)
    SwitchButtonNew mCamPowerBtn;
    @ViewInject(R.id.set_devId)
    TextView setDevId;
    @ViewInject(R.id.nas_setting)
    RelativeLayout nasSetting;
    @ViewInject(R.id.camera_upGrade_new)
    TextView upgradeNew;

    private String deviceId = "";
    private boolean isAlarmOpen;
    private SweetAlertDialog dialog;
    private CommonCommitDialog cusDialog;
    private IMimeCamBusiness business1;
    private String uid = "";
    private CamSettingDataWrapper camSettingDataWrapper;
    private CamLive camLive;
    private int type;

    private static final String INTENT_DEVICEID = "deviceId";


    public static Fragment actionInstance(String deviceId) {//, int devStatus, String camName
        SettingFragment fragment = new SettingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.iermu_setting);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ViewHelper.inject(this, view);
        uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        camSettingDataWrapper = new CamSettingDataWrapper();
        deviceId = getArguments().getString(INTENT_DEVICEID);
        business1 = ErmuBusiness.getMimeCamBusiness();
        camLive = business1.getCamLive(deviceId);
        type = camLive.getConnectType();

        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
//        ErmuBusiness.getMimeCamBusiness().syncLiveStatus(deviceId);
        business.registerListener(OnCamSettingListener.class, this);
        business.registerListener(OnPowerCamListener.class, this);
        business.registerListener(OnRestartCamListener.class, this);
        business.registerListener(OnDropCamListener.class, this);
        business.registerListener(OnCheckCamFirmwareListener.class, this);
        //business.syncCamSetting(deviceId);
//        business.syncCamStatus(deviceId);
        //business.syncCamInfo(deviceId);
        CamInfo camInfo = CamSettingDataWrapper.getCamSettingInfo(uid, deviceId);
        if (camInfo == null) {
            business.syncCamInfo(deviceId);
        } else if (camInfo.getPlatform().equals("100")) {//平台号为100的小球没有nas功能
            nasSetting.setVisibility(View.GONE);
        }
        toastCamStatus();
        initView();
        refreshView();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshView();
        }
    }

    private void refreshView() {
        //CamStatus status = ErmuBusiness.getCamSettingBusiness().getCamStatus(deviceId);
        //CamAlarm camAlarm = ErmuBusiness.getCamSettingBusiness().getCamAlarm(deviceId);
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        boolean conn = Util.isNetworkConn(getActivity());
        //if (status != null) {
        //    boolean power = status.isPower();
        //    mCamPowerBtn.setSwitchOn(power);
        //}
        //if (camAlarm != null) {
        //    isAlarmOpen = camAlarm.isNotice();
        //    mStartPolice.setSwitchOn(camAlarm.isNotice());
        //}
        setDevId.setText(deviceId);
        if (live != null) {
            setCameraName.setText(live.getDescription());
            mCamPowerBtn.setSwitchOn(live.isPowerOn());
            //离线状态 && 没有网络 不能进行操作
            mCamPowerBtn.setFocusable(!live.isOffline() && conn);
            mCamPowerBtn.setEnabled(!live.isOffline() && conn);
            if (live.getConnectType() != ConnectType.BAIDU)
                nasSetting.setVisibility(View.GONE);

            //mStartPolice.setEnabled(!live.isOffline() && conn);
            //mStartPolice.setFocusable(!live.isOffline() && conn);
        }
        if (live.getNeedupdate() == 1) {
            upgradeNew.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        mCamPowerBtn.setOnCheckedChangeListener(this);
        //mStartPolice.setOnCheckedChangeListener(this);
    }

    @OnClick(value = {R.id.set_home, R.id.set_time, R.id.set_more, R.id.set_cloud, R.id.set_camera_message, R.id.nas_setting, R.id.set_cancel_camera, R.id.set_restart_camera, R.id.upgrade_setting})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_home:
                Fragment fragment = EditCamNameFragment.actionInstance(deviceId);//, camName
                ((EditCamNameFragment) fragment).setOnControlListener(this);
                addToBackStack(fragment);
                break;
            case R.id.set_time:
                Fragment fragmentTime = CamOrAlarmCronFragment.actionInstance(getActivity(), CronType.POWER, deviceId);
                addToBackStack(getActivity(), fragmentTime);
                break;
            case R.id.set_more:
                if (!toastCamStatus()) {
                    Fragment fragmentMore = SettingMoreFragment.actionInstance(getActivity(), deviceId);
                    addToBackStack(getActivity(), fragmentMore);
                }
                break;
            case R.id.set_cloud:
                if (!toastCamStatus()) {
                    if (camLive.getConnectType() == ConnectType.LINYANG) {
                        Fragment fragmentCloud = ClouddRecordFragment.actionInstance(getActivity(), deviceId, CronType.CVR);
                        addToBackStack(getActivity(), fragmentCloud);
                    } else {
                        Fragment fragmentCloud = RecordInfoFragment.actionInstance(deviceId);
                        addToBackStack(fragmentCloud);
                    }
                }
                break;
            case R.id.set_camera_message:
                Fragment fragmentMessage = CamInfoFragment.actionInstance(deviceId);//, devStatus
                addToBackStack(getActivity(), fragmentMessage);
                break;
            case R.id.nas_setting:
                if (!toastCamStatus()) {
                    Fragment fragmentNas = NasSettingFragment.actionInstance(deviceId);
                    addToBackStack(getActivity(), fragmentNas);
                }
                break;
            case R.id.upgrade_setting:
                ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
                business.checkCamFirmware(deviceId);
//                ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
//                business.getCamUpdateStatus(deviceId);
                break;
            //case R.id.set_police:
            //if (type == ConnectType.LINYANG) {
            //    Toast toast = Toast.makeText(getActivity(), "正在开发中敬请期待..", Toast.LENGTH_SHORT);
            //    toast.setGravity(Gravity.CENTER, 0, 0);
            //    toast.show();
            //    return;
            //} else {
            //    Fragment fragmentPolice = CamAlarmFramgent.actionInstance(getActivity(), deviceId);
            //    addToBackStack(getActivity(), fragmentPolice);
            //}
            //break;
            //case R.id.set_cron:
            //if (type == ConnectType.LINYANG) {
            //    Toast toast = Toast.makeText(getActivity(), "正在开发中敬请期待..", Toast.LENGTH_SHORT);
            //    toast.setGravity(Gravity.CENTER, 0, 0);
            //    toast.show();
            //    return;
            //} else {
            //    Fragment fragmentCron = CamOrAlarmCronFragment.actionInstance(getActivity(), CronType.ALARM, deviceId);
            //    addToBackStack(getActivity(), fragmentCron);
            //}
            //break;
            case R.id.set_restart_camera:
                if (toastCamStatus()) {
                    return;
                }
                final CommonDialog rsDialog = new CommonDialog(getActivity());
                rsDialog.setCanceledOnTouchOutside(false);
                rsDialog.setTitle(getString(R.string.restart_dev_title));
                rsDialog.setContent(getString(R.string.restart_dev_content));
                rsDialog.setCancelText(getString(R.string.cancle_txt))
                        .setOkText(getString(R.string.now_restart))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ErmuBusiness.getCamSettingBusiness().restartCamDev(deviceId);
                                cusDialog = new CommonCommitDialog(getActivity());
                                cusDialog.show();
                                cusDialog.setStatusText(getString(R.string.restart_dev_current));
                                rsDialog.dismiss();
                            }
                        }).setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rsDialog.dismiss();
                    }
                }).show();
                break;
            case R.id.set_cancel_camera:
                //TODO 注销摄像机啊
                final CommonDialog commonDialog = new CommonDialog(getActivity());
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog.setTitle(getResources().getString(R.string.drop_dev_txt));
                String mText = getResources().getString(R.string.drop_dev_content);
                String mTextLy = getResources().getString(R.string.drop_dev_content_one);
                IMimeCamBusiness business1 = ErmuBusiness.getMimeCamBusiness();
                CamLive camLive = business1.getCamLive(deviceId);
                int type = camLive.getConnectType();
                if (type == ConnectType.LINYANG) {
                    commonDialog.setContent(mTextLy);
                } else {
                    commonDialog.setContent(mText);
                }
                commonDialog.setCancelText(getResources().getString(R.string.hold_on_drop))
                        .setOkText(getResources().getString(R.string.now_drop))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cusDialog = new CommonCommitDialog(getActivity());
                                cusDialog.show();
                                cusDialog.setStatusText(getString(R.string.current_drop_dev));
                                ErmuBusiness.getCamSettingBusiness().dropCamDev(deviceId);
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
    public void onDestroyView() {
        super.onDestroyView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class, this);
        business.unRegisterListener(OnPowerCamListener.class, this);
        business.unRegisterListener(OnRestartCamListener.class, this);
        business.unRegisterListener(OnDropCamListener.class, this);
        business.unRegisterListener(OnCheckCamFirmwareListener.class, this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean conn = Util.isNetworkConn(getActivity());
        switch (buttonView.getId()) {
            case R.id.campower_btn:
                if (conn) {
                    String openDevNow = getResources().getString(R.string.dev_open_now);
                    String closeDevNow = getResources().getString(R.string.dev_close_now);
                    cusDialog = new CommonCommitDialog(getActivity());
                    cusDialog.show();
                    cusDialog.setStatusText(isChecked ? openDevNow : closeDevNow);
                    ErmuBusiness.getCamSettingBusiness().powerCamDev(deviceId, isChecked);
                } else {
                    ErmuApplication.toast(getResources().getString(R.string.no_net));
                }
                break;
        }
    }

    @Override
    public void onPowerCam(Business bus, boolean powerSwitched) {
        if (cusDialog != null) cusDialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
                mCamPowerBtn.setSwitchOn(powerSwitched);
                break;
            case BusinessCode.SUCCESS:
                break;
            default:
                mCamPowerBtn.setSwitchOn(powerSwitched);
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
                break;
        }
    }

    @Override
    public void onRestartCam(Business bus) {
        if (dialog != null) dialog.dismiss();
        if (cusDialog != null) cusDialog.dismiss();
        if (!bus.isSuccess()) {
            ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
        }
    }

    @Override
    public void onDropCam(Business business) {
        if (dialog != null) dialog.dismiss();
        if (cusDialog != null) cusDialog.dismiss();
        if (!business.isSuccess()) {
            ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + business.getErrorCode() + ")");
        } else {
            super.popBackStack();
        }
    }

    //Toast提示信息
    private boolean toastCamStatus() {
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        if (live.isOffline()) { //TODO 离线状态判断放在前面
            ErmuApplication.toast(getResources().getString(R.string.dev_off_line));
            return true;
        } else if (live.isPowerOff()) {
            ErmuApplication.toast(getResources().getString(R.string.dev_power_off));
            return true;
        }
        return false;
    }

    @Override
    public void callBack(String name) {
        setCameraName.setText(name);
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business business) {
        if (!deviceId.equals(deviceId)) return;
        if (cusDialog != null) cusDialog.dismiss();
        int code = business.getCode();
        switch (code) {
            case BusinessCode.SUCCESS:
                CamInfo camInfo = CamSettingDataWrapper.getCamSettingInfo(uid, deviceId);
                if (camInfo != null && camInfo.getPlatform().equals("100")) {//平台号为100的小球没有nas功能
                    nasSetting.setVisibility(View.GONE);
                }
//                refreshView();
                break;
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + business.getErrorCode() + ")");
                break;
        }
    }

    @Override
    public void onCheckCamFirmware(Business bus) {
        if (bus.isSuccess()) {
            ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
            business.getCamUpdateStatus(deviceId);
        }
    }

}
