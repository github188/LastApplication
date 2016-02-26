package com.iermu.ui.fragment.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICloudPaltformBusiness;
import com.iermu.client.listener.OnAddPresetListener;
import com.iermu.client.listener.OnCheckCloudPlatFormListener;
import com.iermu.client.listener.OnCheckIsRotateListener;
import com.iermu.client.listener.OnCloudMoveListener;
import com.iermu.client.listener.OnCloudMovePresetListener;
import com.iermu.client.listener.OnCloudRotateListener;
import com.iermu.client.listener.OnDropPresetListener;
import com.iermu.client.listener.OnListPresetListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CloudPreset;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.event.OnClick;

import java.util.List;

/**
 * Created by zhoushaopei on 15/10/20.
 */
public class TestCloudPlatForm extends BaseFragment implements OnCloudMoveListener, OnCloudRotateListener, OnAddPresetListener, OnDropPresetListener
                    , OnListPresetListener, OnCloudMovePresetListener, OnCheckIsRotateListener, OnCheckCloudPlatFormListener{

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {

    }

    public static Fragment actionInstance(Context ctx) {
        return new TestCloudPlatForm();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_cloud_platform, container, false);
        ViewHelper.inject(this, view);
        ICloudPaltformBusiness cloudPlatFormBusiness = ErmuBusiness.getCloudPlatFormBusiness();
        cloudPlatFormBusiness.registerListener(OnCloudMoveListener.class, this);
        cloudPlatFormBusiness.registerListener(OnCloudRotateListener.class, this);
        cloudPlatFormBusiness.registerListener(OnAddPresetListener.class, this);
        cloudPlatFormBusiness.registerListener(OnDropPresetListener.class, this);
        cloudPlatFormBusiness.registerListener(OnCloudMovePresetListener.class, this);
        cloudPlatFormBusiness.registerListener(OnListPresetListener.class, this);
        cloudPlatFormBusiness.registerListener(OnCheckCloudPlatFormListener.class, this);
        cloudPlatFormBusiness.registerListener(OnCheckIsRotateListener.class, this);
        return view;
    }

    @OnClick(value = {R.id.move, R.id.start_rotate, R.id.close_rotate, R.id.add_preset, R.id.drop_preset, R.id.move_preset
                        , R.id.list_preset, R.id.check_rotate, R.id.check_cloud})
    public void onClick(View view) {
        ICloudPaltformBusiness cloudPlatFormBusiness = ErmuBusiness.getCloudPlatFormBusiness();
        String deviceId = "137893640907";//137893757227     137893640907        137893708859
        switch (view.getId()) {
            case R.id.move:
                cloudPlatFormBusiness.cloudMove(deviceId,500, 840, 463, 0);
                break;
            case R.id.start_rotate:
                cloudPlatFormBusiness.startCloudRotate(deviceId);
                break;
            case R.id.close_rotate:
                cloudPlatFormBusiness.stopCloudRotate(deviceId);
                break;
            case R.id.add_preset:
                cloudPlatFormBusiness.addPreset(deviceId, 1, "门口");
                break;
            case R.id.drop_preset:
                cloudPlatFormBusiness.dropPreset(deviceId,1);
                break;
            case R.id.move_preset:
                cloudPlatFormBusiness.cloudMovePreset(deviceId, 1);
                break;
            case R.id.list_preset:
                cloudPlatFormBusiness.getListPreset(deviceId);
                break;
            case R.id.check_cloud:
                cloudPlatFormBusiness.checkCloudPlatForm(deviceId);
                break;
            case R.id.check_rotate:
                cloudPlatFormBusiness.checkIsRotate(deviceId);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICloudPaltformBusiness cloudPlatFormBusiness = ErmuBusiness.getCloudPlatFormBusiness();
        cloudPlatFormBusiness.unRegisterListener(OnCloudMoveListener.class, (OnCloudMoveListener) this);
        cloudPlatFormBusiness.unRegisterListener(OnAddPresetListener.class, (OnAddPresetListener) this);
        cloudPlatFormBusiness.unRegisterListener(OnDropPresetListener.class, (OnDropPresetListener) this);
        cloudPlatFormBusiness.unRegisterListener(OnDropPresetListener.class, (OnDropPresetListener) this);
        cloudPlatFormBusiness.unRegisterListener(OnCloudMovePresetListener.class, (OnCloudMovePresetListener) this);
        cloudPlatFormBusiness.unRegisterListener(OnListPresetListener.class, (OnListPresetListener) this);
        cloudPlatFormBusiness.unRegisterListener(OnCheckCloudPlatFormListener.class, (OnCheckCloudPlatFormListener) this);
        cloudPlatFormBusiness.unRegisterListener(OnCheckIsRotateListener.class, (OnCheckIsRotateListener) this);
    }

    @Override
    public void onCloudMove(Business business, int num) {
        if (business.getCode() == BusinessCode.SUCCESS) ErmuApplication.toast("成功");
    }

    @Override
    public void onCloudRotate(Business business) {
        if (business.getCode() == BusinessCode.SUCCESS) ErmuApplication.toast("成功");
    }

    @Override
    public void onDropPreset(Business business) {
        if (business.getCode() == BusinessCode.SUCCESS) ErmuApplication.toast("成功");
    }

    @Override
    public void onListPreset(Business business, List<CloudPreset> list, int count) {
        if (business.getCode() == BusinessCode.SUCCESS) ErmuApplication.toast("成功－－"+ count + "----" + list.toString());
        Logger.i(list.toString());
    }

    @Override
    public void onCloudMovePreset(Business business) {
        if (business.getCode() == BusinessCode.SUCCESS) ErmuApplication.toast("成功");
    }

    @Override
    public void onCheckIsRotate(Business business, boolean b) {
        if (b) {
            ErmuApplication.toast("平扫");
        } else {
            ErmuApplication.toast("没有平扫");
        }
    }

    @Override
    public void onAddPreset(Business business, int preset) {
        if (business.getCode() == BusinessCode.SUCCESS) ErmuApplication.toast("成功");
    }

    @Override
    public void onCheckCloudPlatForm(Business business, boolean b, String deviceId) {
        if (b) {
            ErmuApplication.toast("有云台");
        } else {
            ErmuApplication.toast("没有云台");
        }
    }
}
