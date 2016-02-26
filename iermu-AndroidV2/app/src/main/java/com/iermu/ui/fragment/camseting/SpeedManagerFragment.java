package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnSetCamMaxspeedListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamStatus;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.view.CommonCommitDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * 网速管理
 * Created by xjy on 15/6/26.
 */
public class SpeedManagerFragment extends BaseFragment implements BaseFragment.OnCommonClickListener, OnCamSettingListener, OnSetCamMaxspeedListener {

    @ViewInject(R.id.maxupspeed_edt)    EditText maxUpSpeedEdt;
    @ViewInject(R.id.speed_text)        TextView speedText;

    CommonCommitDialog dialog;
    private String deviceId;
    private ICamSettingBusiness business;
    private CamStatus camStatus;
    private static final String INTENT_DEVICEID = "deviceid";
    String namePlate;

    public static Fragment actionInstance(FragmentActivity activity) {
        return new SpeedManagerFragment();
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId) {
        Fragment fragment = new SpeedManagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.set_speed_manager)
                .setCommonBackgroud(R.color.custom_action)
                .setCommonFinish(R.string.text_button)
                .setCommonFinishClick(this)
                .setCommonFinishHided();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_speed_manager, container, false);
        ViewHelper.inject(this, view);
        deviceId = getArguments().getString(INTENT_DEVICEID);
        business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnCamSettingListener.class, this);
        business.registerListener(OnSetCamMaxspeedListener.class, this);
//        business.syncCamSetting(deviceId);
        business.syncCamStatus(deviceId);
        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        InputUtil.hideSoftInput(getActivity(), maxUpSpeedEdt);
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class, this);
        business.unRegisterListener(OnSetCamMaxspeedListener.class, this);
    }

    /*
    切换编辑模式
     */
    private void switchEditMode(boolean edited) {
        String str = maxUpSpeedEdt.getText().toString().trim();
        if (edited) {
            maxUpSpeedEdt.setFocusable(true);
            maxUpSpeedEdt.setFocusableInTouchMode(true);
            InputUtil.showSoftInput(getActivity(), maxUpSpeedEdt);
            maxUpSpeedEdt.findFocus();
            maxUpSpeedEdt.setSelection(str.length());
            super.setCommonFinishShow();
        } else {
            maxUpSpeedEdt.clearFocus();
            maxUpSpeedEdt.setFocusable(false);
            InputUtil.hideSoftInput(getActivity(), maxUpSpeedEdt);
            super.setCommonFinishHided();
        }
    }

    private void refreshView() {
        boolean focusable = maxUpSpeedEdt.isFocusable();
        String str = maxUpSpeedEdt.getText().toString().trim();
        int num = TextUtils.isEmpty(str) ? 0 : Integer.parseInt(str);
        camStatus = ErmuBusiness.getCamSettingBusiness().getCamStatus(deviceId);
        if (camStatus == null || (focusable && num>0)) return;
        maxUpSpeedEdt.setText(Integer.parseInt(camStatus.getMaxspeed()) / 8 + "");
        CamInfo camInfo = ErmuBusiness.getCamSettingBusiness().getCamInfo(deviceId);
        String resolution = (camInfo!=null) ? camInfo.getResolution() : "";
        namePlate = (camInfo!=null) ? camInfo.getNamePlate() : "";
        if ("1080".equals(resolution)){
            speedText.setText(R.string.net_no_more);
        } else {
            speedText.setText(R.string.net_no_more_text);
        }
    }

    @OnClick(value = {R.id.write_btn,R.id.actionbar_back,R.id.actionbar_finish})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_btn:
                switchEditMode(true);
                break;
            case R.id.actionbar_finish:
                switchEditMode(false);
                String str = maxUpSpeedEdt.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    if ("HDP".equals(namePlate)){
                        maxUpSpeedEdt.setText("13");
                    }else {
                        maxUpSpeedEdt.setText("7");
                    }
                    return;
                }
                dialog = new CommonCommitDialog(getActivity());
                dialog.show();
                dialog.setStatusText(getResources().getString(R.string.save_now));
                if ("HDP".equals(namePlate)){
                    if (Integer.valueOf(str) * 8 > 4000){
                        maxUpSpeedEdt.setText("500");
                        setSpeed(500 * 8);
                    }else if (Integer.valueOf(str) * 8 < 100){
                        maxUpSpeedEdt.setText("13");
                        setSpeed(13 * 8);
                    }else {
                        setSpeed(Integer.valueOf(str) * 8);
                    }
                }else {
                    if (Integer.valueOf(str) * 8 > 2000){
                        maxUpSpeedEdt.setText("250");
                        setSpeed(250 * 8);
                    }else if (Integer.valueOf(str) * 8 < 50){
                        maxUpSpeedEdt.setText("7");
                        setSpeed(7 * 8);
                    }else {
                        setSpeed(Integer.valueOf(str) * 8);
                    }
                }
                break;
            case  R.id.actionbar_back:
                popBackStack();
                break;
        }
    }

    private void setSpeed(int num) {
        ErmuBusiness.getCamSettingBusiness().setCamMaxspeed(deviceId, num);
    }

    @Override
    public void onPause() {
        super.onPause();
        InputUtil.hideSoftInput(getActivity(), maxUpSpeedEdt);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        InputUtil.hideSoftInput(getActivity(),maxUpSpeedEdt);
    }

    @Override
    public void onSetCamMaxspeed(Business bus) {
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
                break;
            case BusinessCode.SUCCESS:
                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
                break;
        }
    }

    @Override
    public void onCamSetting(CamSettingType type, String devId, Business business) {
        if (dialog != null) dialog.dismiss();
        if(type == CamSettingType.STATUS && deviceId.equals(devId)) {
            refreshView();
            if (!business.isSuccess()) {
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + business.getErrorCode() + ")");
            }
        }
    }
}