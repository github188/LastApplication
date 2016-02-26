package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnUpdateCamNameListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.util.Util;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * Created by xjy on 15/6/25.
 */
public class EditCamNameFragment extends BaseFragment implements OnUpdateCamNameListener {


    @ViewInject(R.id.set_camera_name)   EditText editText;
    @ViewInject(R.id.actionbar_finish)  TextView mActionbar;
    @ViewInject(R.id.actionbar_back)    ImageView mActionbarBack;
    @ViewInject(R.id.dev_name_close)    ImageView mDeleteName;
    private ICamSettingBusiness business;
    private String deviceId;
    private static final String INTENT_DEVICEID = "deviceid";
    OnDevNameChange listener;

    /**
     * 启动FragmentCameraDescribe
     *
     * @return
     */
    public static Fragment actionInstance(String deviceId) {
        Fragment fragment = new EditCamNameFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_edit);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_camera_describe, container, false);
        ViewHelper.inject(this, view);
        deviceId = getArguments().getString(INTENT_DEVICEID);
        //camName = getArguments().getString(INTENT_CAMNAME);
        business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnUpdateCamNameListener.class, this);
//        business.syncCamSetting(deviceId);
        InputUtil.showSoftInput(getActivity(), editText);
        CamLive camLive = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        editText.setText((camLive != null) ? camLive.getDescription() : "");
        String name = editText.getText().toString().trim();
        if (name.length() > 0 ){
            mDeleteName.setVisibility(View.VISIBLE);
        }else {
            mDeleteName.setVisibility(View.INVISIBLE);
        }
        editText.setSelection(name.length());
        editText.addTextChangedListener(textWatcher);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        InputUtil.hideSoftInput(getActivity(), editText);
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnUpdateCamNameListener.class, this);
    }
    public void setOnControlListener(OnDevNameChange listener) {
        this.listener = listener;
    }

    @OnClick(value = {R.id.actionbar_finish, R.id.actionbar_back,R.id.dev_name_close})
    public void onClick(View v) {
        String name = editText.getText().toString().trim();
        switch (v.getId()) {
            case R.id.actionbar_finish:
                if (Util.isNetworkConn(getActivity())) {
                    ErmuBusiness.getCamSettingBusiness().updateCamName(deviceId, name);
                    InputUtil.hideSoftInput(getActivity(), editText);
                    popBackStack();
                    if (listener != null) {
                        listener.callBack(name);
                    }
                } else {
                    ErmuApplication.toast(getResources().getString(R.string.no_net));
                }
                break;
            case R.id.actionbar_back:
                popBackStack();
                break;
            case R.id.dev_name_close:
                editText.setText(null);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = editText.getText().toString().trim();
            mActionbar.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(name) ) {
//                mActionbar.setEnabled(false);
//                mActionbar.setFocusable(false);
//                mActionbar.setTextColor(getResources().getColor(R.color.wifi_bg));
                mActionbar.setVisibility(View.INVISIBLE);
            } else {
               mActionbar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String name = editText.getText().toString().trim();
            if (name.length() > 31) {
                ErmuApplication.toast(getResources().getString(R.string.describe_length));
            }
            if (name.length() > 0 ){
                mDeleteName.setVisibility(View.VISIBLE);
            }else {
                mDeleteName.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    public void onUpdateCamName(Business bus) {
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
                break;
            case BusinessCode.SUCCESS:
                ErmuApplication.toast(getResources().getString(R.string.update_name_success));
                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        InputUtil.hideSoftInput(getActivity(),editText);
    }

    public interface OnDevNameChange {
        void callBack(String name);
    }
}
