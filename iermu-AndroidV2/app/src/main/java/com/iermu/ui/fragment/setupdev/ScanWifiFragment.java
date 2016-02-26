package com.iermu.ui.fragment.setupdev;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.util.WifiUtil;
import com.iermu.ui.adapter.WifiAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.LoadingView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;
import com.viewinject.annotation.event.OnItemClick;

import java.util.List;

/**
 * 扫描Wifi列表页面
 * <p/>
 * Created by zhoushaopei on 15/6/23.
 */
public class ScanWifiFragment extends BaseFragment  {

    @ViewInject(R.id.list)              ListView mWifiList;
    @ViewInject(R.id.load_view)         LoadingView loadView;
    @ViewInject(R.id.wifi_item)         RelativeLayout wifiItem;
    @ViewInject(R.id.wifi_num)          TextView wifiNum;
    @ViewInject(R.id.actionbar_title)   TextView mTitle;
    @ViewInject(R.id.actionbar_back)    ImageView mBack;
    @ViewInject(R.id.actionbar_finish)  TextView mFinish;

    private WifiAdapter mAdapter;
    private OnWifiChecked listener;
    private WifiInfo wifiInfo;

    public static ScanWifiFragment actionInstance(FragmentActivity activity) {
        return new ScanWifiFragment();
    }

    public void registerWifiChecked(OnWifiChecked lis) {
        this.listener = lis;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
//        setCommonActionBar(R.string.conn_wifi_txt);
        setCustomActionBar(R.layout.actionbar_about);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        mTitle.setText(getResources().getString(R.string.update_wifi_text));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        loadView.startAnimation();
        View headerView = View.inflate(getActivity(), R.layout.wifi_header, null);
        mWifiList.addHeaderView(headerView);
        mAdapter = new WifiAdapter(getActivity());
        mWifiList.setAdapter(mAdapter);
        ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
        devBusiness.addSetupDevListener(mListener);
        devBusiness.scanWifi();
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(getActivity().WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
        devBusiness.quitScanWifi();
        devBusiness.removeSetupDevListener(mListener);
    }

    @OnItemClick(R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ScanResult wifi = (ScanResult) mAdapter.getItem(position - 1);
        if(WifiUtil.is5GHz(wifi.frequency)){
            return;
        }
        if (listener != null) listener.callback(wifi);
        popBackStack();
    }

    private void refreshView() {
        List<ScanResult> list = ErmuBusiness.getSetupDevBusiness().getScanWifi();
        loadView.setVisibility((list.size() > 0) ? View.GONE : View.VISIBLE);
        mAdapter.notifyDataSetChanged(list);
    }
    @OnClick(value = {R.id.actionbar_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_back:
                popBackStack();
                //ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
                //devBusiness.quitScanCam();
                break;
        }
    }

    OnSetupDevListener mListener = new OnSetupDevListener() {
        @Override
        public void onScanWifiList() {
            super.onScanWifiList();
            refreshView();
        }

        @Override
        public void onScanFail(int businessCode, String message) {
            super.onScanFail(businessCode, message);
            refreshView();
            switch (businessCode) {
            case BusinessCode.WIFI_CLOSE:
                ErmuApplication.toast(getResources().getString(R.string.no_network));
                loadView.setVisibility(View.GONE);
                break;
            }
        }
    };

    interface OnWifiChecked {
        void callback(ScanResult result);
    }
}
