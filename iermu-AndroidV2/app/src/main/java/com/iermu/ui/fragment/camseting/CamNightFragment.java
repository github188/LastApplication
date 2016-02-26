package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnSetDevExposeModeListener;
import com.iermu.client.listener.OnSetDevNightModeListener;
import com.iermu.client.listener.OnSetDevSceneListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamStatus;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.CommonCommitDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjy on 15/6/26.
 */
public class CamNightFragment extends BaseFragment implements View.OnClickListener, OnCamSettingListener
                                            , OnSetDevNightModeListener, OnSetDevExposeModeListener, OnSetDevSceneListener {
    @ViewInject(R.id.set_use_in)        RelativeLayout setUserIn;
    @ViewInject(R.id.set_use_out)       RelativeLayout setUserOut;
    @ViewInject(R.id.night_automatic)   RelativeLayout automatic;
    @ViewInject(R.id.night_start)       RelativeLayout nightStart;
    @ViewInject(R.id.night_close)       RelativeLayout nightClose;
    @ViewInject(R.id.night_mode_automatic)  RelativeLayout ziDong;
    @ViewInject(R.id.night_hight)       RelativeLayout nightHight;
    @ViewInject(R.id.night_low)         RelativeLayout nightLow;

    public static Fragment actionInstance(FragmentActivity activity) {
        return new CamNightFragment();
    }
    public static Fragment actionInstance(FragmentActivity activity,String deviceId){
        Fragment fragment = new CamNightFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(getString(R.string.iermu_night_sun));
    }

    private List<View> items = new ArrayList<View>();
    private List<View> realts = new ArrayList<View>();
    private List<View> modes = new ArrayList<View>();
    private CamStatus camNight;
    private static final String INTENT_DEVICEID = "deviceId";
    private ICamSettingBusiness business;

    private String deviceId;
    CommonCommitDialog dialog;
    CamStatus camStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_night_exposure, container, false);
        ViewHelper.inject(this, view);
        initView();
        initData();
        initMode();
        deviceId = getArguments().getString(INTENT_DEVICEID);
        business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnSetDevSceneListener.class, this);
        business.registerListener(OnCamSettingListener.class, this);
        business.registerListener(OnSetDevNightModeListener.class,this);
        business.registerListener(OnSetDevExposeModeListener.class,this);
//        business.syncCamSetting(deviceId);
        business.syncCamStatus(deviceId);
        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class, this);
        business.unRegisterListener(OnSetDevSceneListener.class, this);
        business.unRegisterListener(OnSetDevNightModeListener.class, this);
        business.unRegisterListener(OnSetDevExposeModeListener.class, this);
    }

    private void initData() {
        realts.add(automatic);
        realts.add(nightStart);
        realts.add(nightClose);
        for (int j = 0; j < realts.size(); j++) {
            realts.get(j).setOnClickListener(realtslistener);
        }
    }

    private void initView() {
        items.add(setUserIn);
        items.add(setUserOut);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setOnClickListener(itemslistener);
        }
    }

    private void initMode() {
        modes.add(ziDong);
        modes.add(nightHight);
        modes.add(nightLow);
        for (int m = 0; m < modes.size(); m++) {
            modes.get(m).setOnClickListener(modeslistener);
        }
    }

    private void refreshView() {
        camStatus = ErmuBusiness.getCamSettingBusiness().getCamStatus(deviceId);
        if (camStatus == null) return;
        switchSceneView(camStatus.getScene());
        switchNightView(camStatus.getNightmode());
        switchExposeView(camStatus.getExposemode());
    }

    //切换室内室外
    private void switchSceneView(int scene) {
        boolean useIn = (scene == 0);
        setUserIn.findViewById(R.id.workcycle_selected_img).setVisibility(useIn ? View.VISIBLE : View.INVISIBLE);
        setUserOut.findViewById(R.id.workcycle_selected_img).setVisibility(useIn ? View.INVISIBLE : View.VISIBLE);
    }

    //切换夜视模式
    private void switchNightView(int nightMode) {
        if (nightMode == 0) {
            automatic.findViewById(R.id.night_img).setVisibility(View.VISIBLE);
            nightClose.findViewById(R.id.night_img).setVisibility(View.INVISIBLE);
            nightStart.findViewById(R.id.night_img).setVisibility(View.INVISIBLE);
        } else if (nightMode == 1) {
            automatic.findViewById(R.id.night_img).setVisibility(View.INVISIBLE);
            nightClose.findViewById(R.id.night_img).setVisibility(View.VISIBLE);
            nightStart.findViewById(R.id.night_img).setVisibility(View.INVISIBLE);
        } else if (nightMode == 2) {
            automatic.findViewById(R.id.night_img).setVisibility(View.INVISIBLE);
            nightClose.findViewById(R.id.night_img).setVisibility(View.INVISIBLE);
            nightStart.findViewById(R.id.night_img).setVisibility(View.VISIBLE);
        }
    }

    //切换曝光模式
    private void switchExposeView(int exposeMode) {

        if (exposeMode == 0) {//自动
            ziDong.findViewById(R.id.night_mode_img).setVisibility(View.VISIBLE);
            nightHight.findViewById(R.id.night_mode_img).setVisibility(View.INVISIBLE);
            nightLow.findViewById(R.id.night_mode_img).setVisibility(View.INVISIBLE);
        } else if (exposeMode == 1) {//高光
            ziDong.findViewById(R.id.night_mode_img).setVisibility(View.INVISIBLE);
            nightHight.findViewById(R.id.night_mode_img).setVisibility(View.VISIBLE);
            nightLow.findViewById(R.id.night_mode_img).setVisibility(View.INVISIBLE);
        } else if (exposeMode == 2) {//低光
            ziDong.findViewById(R.id.night_mode_img).setVisibility(View.INVISIBLE);
            nightHight.findViewById(R.id.night_mode_img).setVisibility(View.INVISIBLE);
            nightLow.findViewById(R.id.night_mode_img).setVisibility(View.VISIBLE);
        }
    }

    public View.OnClickListener itemslistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            showDialog();
            switch (view.getId()) {
                case R.id.set_use_in:
                    switchSceneView(0);
                    ErmuBusiness.getCamSettingBusiness().setDevScene(deviceId, false);
                    break;
                case R.id.set_use_out:
                    switchSceneView(1);
                    ErmuBusiness.getCamSettingBusiness().setDevScene(deviceId, true);
                    break;
            }
        }
    };
    public View.OnClickListener realtslistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            showDialog();
            switch (v.getId()) {
                case R.id.night_automatic:
                    switchNightView(0);
                    ErmuBusiness.getCamSettingBusiness().setDevNightMode(deviceId, 0);
                    break;
                case R.id.night_close:
                    switchNightView(1);
                    ErmuBusiness.getCamSettingBusiness().setDevNightMode(deviceId, 1);
                    break;
                case R.id.night_start:
                    switchNightView(2);
                    ErmuBusiness.getCamSettingBusiness().setDevNightMode(deviceId, 2);
                    break;
            }
        }
    };
    public View.OnClickListener modeslistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            showDialog();
            switch (v.getId()) {
                case R.id.night_mode_automatic:
                    switchExposeView(0);
                    ErmuBusiness.getCamSettingBusiness().setDevExposeMode(deviceId, 0);
                    break;
                case R.id.night_hight:
                    switchExposeView(1);
                    ErmuBusiness.getCamSettingBusiness().setDevExposeMode(deviceId, 1);
                    break;
                case R.id.night_low:
                    switchExposeView(2);
                    ErmuBusiness.getCamSettingBusiness().setDevExposeMode(deviceId, 2);
                    break;
            }
        }
    };


    private void showDialog() {
        dialog = new CommonCommitDialog(getActivity());
        dialog.show();
        dialog.setStatusText(getString(R.string.dialog_commit));
    }



    @Override
    public void onSetDevExposeMode(Business bus) {
        boolean success = (bus.getCode()== BusinessCode.SUCCESS);
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
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
    public void onSetDevNightMode(Business bus) {
        boolean success = (bus.getCode()== BusinessCode.SUCCESS);
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
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
    public void onSetDevScene(Business bus) {
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
        case BusinessCode.CONNECT_API_FAILED:
        case BusinessCode.SEND_COMMAND_FAILED:
        case BusinessCode.UPDATE_DEVSETTING_FAILED:
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
    public void onClick(View v) {
        return;
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business business) {
        if(dialog != null) dialog.dismiss();
        refreshView();
        if (!business.isSuccess()){
            ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+business.getErrorCode()+")");
        }
    }
}
