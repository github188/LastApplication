package com.iermu.ui.fragment.setupdev;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.event.OnClick;

/**
 * Created by xjy on 15/9/18.
 */
public class ConnAndBuyFragment extends BaseFragment {

    public  static boolean isFirst;
    public static boolean isInput;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        hideActionBar();
    }

    public static Fragment actionInstance(Context context) {
        return new ConnAndBuyFragment();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        hideActionBar();
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        hideActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_dev, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        isFirst = true;
        isInput = true;
        ErmuBusiness.getPreferenceBusiness().setInputWifiPa(isInput);
        ErmuBusiness.getPreferenceBusiness().setRebBlueLight(isFirst);
        ErmuBusiness.getPreferenceBusiness().setDevAddErrorTimes(0);
//        WifiNetworkManager wifiManager = WifiNetworkManager.getInstance(getActivity());
//        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.openWifi();
//        }
        //ErmuBusiness.getSetupDevBusiness().scanWifi();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ErmuBusiness.getSetupDevBusiness().quitSetupDev();
    }

    @OnClick(value = {R.id.conn_dev_text, R.id.buy_dev_text, R.id.add_dev_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.conn_dev_text:
                Fragment fragment1 = SearchGuideDevFragment.actionInstance(getActivity());
//                Fragment fragment1 = ResetDevFragment.actionInstance();
                super.addToBackStack(fragment1);
                break;
            case R.id.buy_dev_text:
                WebActivity.actionStartWeb(getActivity(), WebActivity.BUG_CAM);
                break;
            case R.id.add_dev_back:
                popBackStack();
                break;
        }
    }
}
