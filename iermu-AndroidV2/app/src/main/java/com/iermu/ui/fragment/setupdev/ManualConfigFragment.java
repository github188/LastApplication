package com.iermu.ui.fragment.setupdev;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 *
 * 去配置界面
 * Created by xjy on 15/9/24.
 */
public class ManualConfigFragment extends BaseFragment {

    private static final String KEY_CAMDEVCONF  = "camdevconf";
    private static final String KEY_CAMDEV      = "camdev";

    @ViewInject(R.id.config_btn)        Button mConfigBtn;
    @ViewInject(R.id.change_wifi)       TextView mChangeWifi;
    @ViewInject(R.id.cancel)            TextView mCancel;
    @ViewInject(R.id.actionbar_back)    ImageView mActionbarBack;

    private CamDev itemCamDev;
    private String wifiSSID;
    //private String bSSID;
    private String devSSID;
    //private List<CamDev> mCamDev;
    private  CamDevConf camDevConf;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_add_camera);
    }

    public static Fragment actionInstance(CamDev item, CamDevConf camDevConf) {
        ManualConfigFragment fragment = new ManualConfigFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CAMDEV, item);
        bundle.putSerializable(KEY_CAMDEVCONF, camDevConf);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        mCancel.setVisibility(View.INVISIBLE);
        mActionbarBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual, container, false);
        ViewHelper.inject(this, view);

        mChangeWifi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        initView();
        return view;
    }

    private void initView () {
        Bundle bundle = getArguments();
        itemCamDev = (CamDev) bundle.getSerializable(KEY_CAMDEV);
        camDevConf = (CamDevConf)bundle.getSerializable(KEY_CAMDEVCONF);
        devSSID = itemCamDev.getSSID();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mConfigBtn != null) refreshView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refreshView();
    }

    @OnClick(value = {R.id.config_btn, R.id.change_wifi,R.id.actionbar_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.config_btn:
            String text = mConfigBtn.getText().toString().trim();
            if (text.contains("iermu")) {
                openSetting();
            } else {
//                ScanResult scanResult = WifiNetworkManager.getInstance(getActivity()).findScanResult(wifiSSID);
//                CamDevConf camDevConf  = new CamDevConf(scanResult,wifiAccount,wifiPass, ipText,netmaskText,gatewayTtext);
//                WifiTransferModel wifiInfo = new WifiTransferModel(wifiSSID,wifiPass,wifiAccount,isAdvanceCon, ipText, netmaskText, gatewayTtext,wifiType);
                Fragment fragment = ConnDevFragment.actionInstance(itemCamDev, camDevConf);
                super.addToBackStack(fragment);
            }
            break;
            case R.id.change_wifi:
            Fragment fragment = ChangeWiFiFragment.actionInstance(getActivity());
            super.addToBackStack(fragment);
            break;
            case R.id.actionbar_back:
            popBackStack();
            break;
        }
    }

//    android.os.Handler handler = new android.os.Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == CONFIG) {
//                refreshView();
//            }
//            super.handleMessage(msg);
//        }
//    };

    private void refreshView () {
        wifiSSID = ErmuBusiness.getSetupDevBusiness().scanConnectSSID();
        //bSSID = ErmuBusiness.getSetupDevBusiness().scanConnectBSSID();
        //boolean ipcAP1 = CmsMenu.isIpcAP(wifiSSID, bSSID);
        if (wifiSSID.equals(devSSID)) {
            mConfigBtn.setText(R.string.conn_dev_wifi_ok);
        } else {
            mConfigBtn.setText(getResources().getString(R.string.goto_connect) +" " + devSSID +" " + getResources().getString(R.string.wifi));
        }
    }

    private void openSetting() {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            ClipboardManager cmb11 = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb11.setText("cmsiermu2013");//ACTION_WIFI_SETTINGS
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            android.text.ClipboardManager cmb19 = (android.text.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb19.setText("cmsiermu2013");
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

}
