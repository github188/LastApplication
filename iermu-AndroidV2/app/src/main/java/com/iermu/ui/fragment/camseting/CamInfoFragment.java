package com.iermu.ui.fragment.camseting;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.business.dao.CamSettingDataWrapper;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnCheckCamFirmwareListener;
import com.iermu.client.listener.OnCheckUpgradeVersionListener;
import com.iermu.client.listener.OnGetCamUpdateStatusListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.UpgradeVersion;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.viewmodel.CamUpdateStatus;
import com.iermu.client.model.viewmodel.MimeCamItem;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.MainIermuFragment;
import com.iermu.ui.view.AcceptAuthDialog;
import com.iermu.ui.view.UpgradeTipDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * 摄像机信息页
 * <p/>
 * Created by xjy on 15/6/25.
 */
public class CamInfoFragment extends BaseFragment implements OnCamSettingListener,OnCheckUpgradeVersionListener {

    private static final String KEY_DEVICEID = "deviceId";
//    private static final String KEY_DEVSTATUS = "devstatus";

    @ViewInject(R.id.cammodel_tv)
    TextView camModelTv;
    @ViewInject(R.id.camsn_tv)
    TextView camSnTv;
    @ViewInject(R.id.cammac_tv)
    TextView camMacTv;
    @ViewInject(R.id.camwifi_tv)
    TextView camWifiTv;
    @ViewInject(R.id.camip_tv)
    TextView camIpTv;
    @ViewInject(R.id.camsig_tv)
    TextView camSigTv;
    @ViewInject(R.id.camlight_tv)
    TextView camLightTv;
    @ViewInject(R.id.camstatus_tv)
    TextView camStatusTv;
    @ViewInject(R.id.camversion_tv)
    TextView camVersionTv;
    @ViewInject(R.id.camera_upgrade_new)
    TextView upgradeNew;
    @ViewInject(R.id.camera_version)
    RelativeLayout cameraVersion;

    private String deviceId;
    //    private Integer devStatus;
    private CamInfo camInfo;
    private ICamSettingBusiness business;

    UpgradeTipDialog upDialog;


    public static Fragment actionInstance(String deviceId) {//,int devStatus
        CamInfoFragment fragment = new CamInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEVICEID, deviceId);
//        bundle.putInt(KEY_DEVSTATUS,devStatus);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(getString(R.string.iermu_camera_message));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_caminfo, container, false);
        ViewHelper.inject(this, view);
        Bundle bundle = getArguments();
        deviceId = bundle.getString(KEY_DEVICEID);
//        devStatus = bundle.getInt(KEY_DEVSTATUS);
        business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnCamSettingListener.class, this);
        business.registerListener(OnCheckUpgradeVersionListener.class, this);
//        business.syncCamSetting(deviceId);
        business.syncCamInfo(deviceId);
        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class, this);
        business.unRegisterListener(OnCheckUpgradeVersionListener.class, this);
    }

    @OnClick(value = {R.id.camera_version})
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.camera_version:
                CamLive camLive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
                if(camLive.getNeedupdate()==1&&MainIermuFragment.upgradeCountMap.get(deviceId)==null){
                    if(camLive.isPowerOff())
                        ErmuApplication.toast(getString(R.string.cam_turn_off));
                    else if (camLive.isOffline())
                        ErmuApplication.toast(getString(R.string.cam_off_ine));
                    else
                        business.checkUpgradeVersion(deviceId);
                    break;
                }

        }
    }

    private void showUpgradeTipDialog(String version, String[] tip) {
        upDialog = new UpgradeTipDialog(getActivity(), version, tip);
        upDialog.setClicklistener(new UpgradeTipDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                upDialog.dismiss();
                CamLive camLive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
                if(camLive.getDataType()== MimeCamItem.TYPE_MIME){
                    ErmuBusiness.getCamSettingBusiness().checkCamFirmware(deviceId);
                    popBackAllStack();
                }
            }

            @Override
            public void doCancel() {
                upDialog.dismiss();
            }
        });
        upDialog.show();
    }

    private void refreshView() {
        String uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        camInfo = CamSettingDataWrapper.getCamSettingInfo(uid, deviceId);
        CamLive camLive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        if (camInfo == null) return;
        //String status = devStatus > 0 ? (((devStatus >> 2) & 1) == 1 ? "工作中" : "已关机") : "离线";
        String stateOn = getString(R.string.on_line);
        String powerOff = getString(R.string.power_off);
        String offLine = getString(R.string.off_line);
        String status = camLive.isOffline() ? offLine : (camLive.isPowerOff() ? powerOff : stateOn);
        String resolution = camInfo.getResolution();
        camModelTv.setText(resolution + getString(R.string.p_cam));
        camSnTv.setText(deviceId);
        camMacTv.setText(camInfo.getMac());
        camWifiTv.setText(camInfo.getWifi());
        camIpTv.setText(camInfo.getIp());
        camSigTv.setText(camInfo.getSig() + "%");
        camStatusTv.setText(status);
        camVersionTv.setText(camInfo.getFirmware() + "-" + camInfo.getFirmdate());
        if(camLive.getNeedupdate()==1){
            upgradeNew.setVisibility(View.VISIBLE);
            cameraVersion.setClickable(true);
        }
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business bus) {
        if (bus.getCode() == BusinessCode.SUCCESS) {
            camModelTv.setVisibility(View.VISIBLE);
            refreshView();
        } else {
            ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
        }
    }

    @Override
    public void OnCheckUpgradeVersion(UpgradeVersion upgradeVersion, Business business) {
        if(business.isSuccess()&&upgradeVersion!=null){
            String desc = upgradeVersion.getDesc();
            String[] descs = desc.split("\n");
            if(upDialog==null||!upDialog.isShowing())
            showUpgradeTipDialog(upgradeVersion.getVersion(),descs);

        }


    }
}
