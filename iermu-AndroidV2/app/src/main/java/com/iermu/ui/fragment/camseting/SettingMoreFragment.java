package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnSetDevAudioListener;
import com.iermu.client.listener.OnSetDevInvertListener;
import com.iermu.client.listener.OnSetDevLightListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamStatus;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.SettingButtonLoad;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * Created by xjy on 15/6/25.
 */
public class SettingMoreFragment extends BaseFragment implements View.OnClickListener, OnCamSettingListener
        , CompoundButton.OnCheckedChangeListener, OnSetDevLightListener
        , OnSetDevInvertListener, OnSetDevAudioListener {

    @ViewInject(R.id.set_speed_manager) RelativeLayout setSpeedManager;
    @ViewInject(R.id.set_night_sun)     RelativeLayout setNightSun;
    @ViewInject(R.id.camlight_btn)      SwitchButtonNew camLightBtn;
    @ViewInject(R.id.caminvert_btn)     SwitchButtonNew camInvertBtn;
    @ViewInject(R.id.camaudio_btn)      SwitchButtonNew camAudioBtn;
    @ViewInject(R.id.switchbutton_loading)
    SettingButtonLoad buttonLoad;
    @ViewInject(R.id.switchbutton_loading_1)
    SettingButtonLoad buttonLoad1;
    @ViewInject(R.id.switchbutton_loading_2)
    SettingButtonLoad buttonLoad2;

    private CommonCommitDialog dialog;
    private String deviceId;
    private CamStatus camStatus;
    private ICamSettingBusiness business;
    private static final String INTENT_DEVICEID = "deviceId";

    public static Fragment actionInstance(FragmentActivity activity) {
        return new SettingMoreFragment();
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId) {
        Fragment fragment = new SettingMoreFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.iermu_more_setting);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_more_set, container, false);
        ViewHelper.inject(this, view);
        camLightBtn.setOnCheckedChangeListener(this);
        camInvertBtn.setOnCheckedChangeListener(this);
        camAudioBtn.setOnCheckedChangeListener(this);
        deviceId = getArguments().getString(INTENT_DEVICEID);
        business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnCamSettingListener.class, this);
        business.registerListener(OnSetDevLightListener.class, this);
        business.registerListener(OnSetDevAudioListener.class, this);
        business.registerListener(OnSetDevInvertListener.class, this);
//        business.syncCamSetting(deviceId);
        business.syncCamStatus(deviceId);
        camAudioBtn.setVisibility(View.INVISIBLE);
        camInvertBtn.setVisibility(View.INVISIBLE);
        camLightBtn.setVisibility(View.INVISIBLE);
        buttonLoad.setVisibility(View.VISIBLE);
        buttonLoad1.setVisibility(View.VISIBLE);
        buttonLoad2.setVisibility(View.VISIBLE);
        buttonLoad.startAnimation();
        buttonLoad1.startAnimation();
        buttonLoad2.startAnimation();
        IMimeCamBusiness business1 = ErmuBusiness.getMimeCamBusiness();
        CamLive camLive = business1.getCamLive(deviceId);
        int connectType = camLive.getConnectType();
//        if (connectType == ConnectType.LINYANG) {
//            setSpeedManager.setVisibility(View.GONE);
//        } else {
//            setSpeedManager.setVisibility(View.VISIBLE);
//        }
        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class, this);
        business.unRegisterListener(OnSetDevLightListener.class, this);
        business.unRegisterListener(OnSetDevInvertListener.class,this);
        business.unRegisterListener(OnSetDevAudioListener.class,this);
    }

    @OnClick(value = {R.id.set_speed_manager, R.id.set_night_sun})
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.set_speed_manager:
            Fragment fragmentSpeed = SpeedManagerFragment.actionInstance(getActivity(),deviceId);
            addToBackStack(fragmentSpeed);
            break;
        case R.id.set_night_sun:
            Fragment fragmentNight = CamNightFragment.actionInstance(getActivity(), deviceId);
            addToBackStack(fragmentNight);
            break;
        }
    }

    private void refreshView() {
        camStatus = ErmuBusiness.getCamSettingBusiness().getCamStatus(deviceId);
        if (camStatus == null) return;
        camLightBtn.setSwitchOn(camStatus.isLight());
        camInvertBtn.setSwitchOn(camStatus.isInvert());
        camAudioBtn.setSwitchOn(camStatus.isAudio());
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        dialog = new CommonCommitDialog(getActivity());
        dialog.show();
        dialog.setStatusText(getResources().getString(R.string.dialog_commit));
        switch (buttonView.getId()) {
        case R.id.camlight_btn:
            ErmuBusiness.getCamSettingBusiness().setDevLight(deviceId, isChecked);
            break;
        case R.id.caminvert_btn:
            ErmuBusiness.getCamSettingBusiness().setDevInvert(deviceId, isChecked);
            break;
        case R.id.camaudio_btn:
            ErmuBusiness.getCamSettingBusiness().setDevAudio(deviceId, isChecked);
            break;
        }
    }
    @Override
    public void onSetDevInvert(Business bus,boolean invert) {
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                camInvertBtn.setSwitchOn(invert);
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                break;
            case BusinessCode.SUCCESS:
                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                break;
        }
    }
    @Override
    public void onSetDevLight(Business bus,boolean light) {
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                camLightBtn.setSwitchOn(light);
                break;
            case BusinessCode.SUCCESS:
                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                camLightBtn.setSwitchOn(light);
                break;
        }
    }

    @Override
    public void onSetDevAudio(Business bus, boolean audio) {
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                camAudioBtn.setSwitchOn(audio);
                break;
            case BusinessCode.SUCCESS:
                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                camAudioBtn.setSwitchOn(audio);
                break;
        }
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business business) {
        if(dialog != null) dialog.dismiss();
        buttonLoad.stopAnimation();
        buttonLoad1.stopAnimation();
        buttonLoad2.stopAnimation();
        buttonLoad1.setVisibility(View.INVISIBLE);
        buttonLoad2.setVisibility(View.INVISIBLE);
        buttonLoad.setVisibility(View.INVISIBLE);
        camLightBtn.setVisibility(View.VISIBLE);
        camInvertBtn.setVisibility(View.VISIBLE);
        camAudioBtn.setVisibility(View.VISIBLE);
        refreshView();
    }
}
