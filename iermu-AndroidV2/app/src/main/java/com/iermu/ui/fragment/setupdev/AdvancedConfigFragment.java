package com.iermu.ui.fragment.setupdev;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.business.api.CamDeviceApi;
import com.iermu.client.business.api.response.RegisterDevResponse;
import com.iermu.client.business.impl.PreferenceBusImpl;
import com.iermu.client.business.impl.setupdev.setup.SetupStatus;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 高级配置
 * Created by xjy on 15/9/24.
 */
public class AdvancedConfigFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {

    private static final String DEVICEID = "deviceid";
    @ViewInject(R.id.ip_lay)        LinearLayout ipLay;
    @ViewInject(R.id.netmask_lay)   LinearLayout netmaskLay;
    @ViewInject(R.id.gateway_lay)   LinearLayout gatewayLay;
    @ViewInject(R.id.ip_mode)       SwitchButtonNew ipMode;
    @ViewInject(R.id.ip_txt)        EditText mIp;
    @ViewInject(R.id.netmask)       EditText mNetmask;
    @ViewInject(R.id.gateway)       EditText mGateway;
    @ViewInject(R.id.advance_config)SwitchButtonNew mAdvanceConfig;

    private WifiManager wifiManager;

    JSONObject ipModeJson = null;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(getResources().getString(R.string.advance_config));
    }

    @Override
    public void onActionBarCreated(View view) {
        ViewHelper.inject(this, view);
    }

    public static AdvancedConfigFragment  actionInstance() {
        AdvancedConfigFragment fragment = new AdvancedConfigFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advance,container,false);
        ViewHelper.inject(this,view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        initView();
        return view;
    }

    private void initView() {
        //恢复切换按钮状态
        boolean advancedConfig = ErmuBusiness.getPreferenceBusiness().getAdvancedConfig();
        mAdvanceConfig.setSwitchOn(advancedConfig);
        ipMode.setOnCheckedChangeListener(this);
        mAdvanceConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ErmuBusiness.getPreferenceBusiness().setAdvanceConfig(isChecked);
            }
        });

        String ipModeString = ErmuBusiness.getPreferenceBusiness().getIpMode();
        if(!"".equals(ipModeString)){
            try {
                ipModeJson = new JSONObject(ipModeString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            initIpMode(ipModeJson);
        }



    }

    @OnClick(value = {R.id.actionbar_back})
    public void onClick(View v){
        switch (v.getId()){
        case R.id.actionbar_back:
            InputUtil.hideSoftInput(getActivity(),mIp);
            InputUtil.hideSoftInput(getActivity(),mNetmask);
            InputUtil.hideSoftInput(getActivity(),mGateway);
            getDevIp();
            popBackStack();
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getDevIp();
            popBackStack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initIpMode(JSONObject ipModeJson) {
        try {
            if(ipModeJson.getBoolean("isIpMode")){
                ipMode.setSwitchOn(true);
                ipLay.setVisibility(View.VISIBLE);
                netmaskLay.setVisibility(View.VISIBLE);
                gatewayLay.setVisibility(View.VISIBLE);
                mIp.setText(ipModeJson.getString("ip"));
                mNetmask.setText(ipModeJson.getString("netmask"));
                mGateway.setText(ipModeJson.getString("gateway"));
                if (listener != null) {
                    String ip = mIp.getText().toString().trim();
                    String netmask = mNetmask.getText().toString().trim();
                    String gateway = mGateway.getText().toString().trim();
                    if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(netmask) && !TextUtils.isEmpty(gateway)) {
                        listener.ipconfig(ip, netmask, gateway);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            ipLay.setVisibility(View.VISIBLE);
            netmaskLay.setVisibility(View.VISIBLE);
            gatewayLay.setVisibility(View.VISIBLE);
            mIp.setText(getIpNet());
            mNetmask.setText(subNet());
            mGateway.setText(defaultNet());
            if (listener != null) {
                String ip = mIp.getText().toString().trim();
                String netmask = mNetmask.getText().toString().trim();
                String gateway = mGateway.getText().toString().trim();
                if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(netmask) && !TextUtils.isEmpty(gateway)) {
                    listener.ipconfig(ip, netmask, gateway);
                }
            }
        } else {
            ipLay.setVisibility(View.GONE);
            netmaskLay.setVisibility(View.GONE);
            gatewayLay.setVisibility(View.GONE);

            if (listener != null) {
                listener.ipconfig("", "", "");
            }

        }
    }

    private void getDevIp() {
        if (!(ipMode.isChecked()) && listener != null) {
            String ip = mIp.getText().toString().trim();
            String netmask = mNetmask.getText().toString().trim();
            String gateway = mGateway.getText().toString().trim();
            if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(netmask) && !TextUtils.isEmpty(gateway)) {
                JSONObject js = new JSONObject();
                try {
                    js.put("isIpMode",!ipMode.isChecked());
                    js.put("ip",ip);
                    js.put("netmask",netmask);
                    js.put("gateway",gateway);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ErmuBusiness.getPreferenceBusiness().setIpMode(js.toString());
                listener.ipconfig(ip, netmask, gateway);
            }
        }
    }

    private boolean isIP(String ipNet) {
        String regex = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
        return ipNet.matches(regex);
    }

    private String getIpNet() {
        if (!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }

    private String subNet() {
        DhcpInfo di = wifiManager.getDhcpInfo();
        long netmaskIpL=di.netmask;
        return long2ip(netmaskIpL);
    }

    private String defaultNet() {
        DhcpInfo di = wifiManager.getDhcpInfo();
        long getewayIpL=di.gateway;
        return long2ip(getewayIpL);
    }

    private String long2ip(long ip){
        StringBuffer sb=new StringBuffer();
        sb.append(String.valueOf((int)(ip&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>8)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>16)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    private AdvancedIp listener;
    public void registerAdvance(AdvancedIp lis) {
        this.listener = lis;
    }
    interface AdvancedIp{
        void ipconfig(String ip, String netmask, String gareway);
    }

}
