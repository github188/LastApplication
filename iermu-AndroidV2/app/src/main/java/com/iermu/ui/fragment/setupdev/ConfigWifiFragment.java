package com.iermu.ui.fragment.setupdev;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cms.iermu.cms.CmsUtil;
import com.cms.iermu.cms.WifiType;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.business.dao.WifiInfoWrapper;
import com.iermu.client.business.impl.setupdev.WifiNetworkManager;
import com.iermu.client.model.CamDevConf;
import com.iermu.client.util.LanguageUtil;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.ResizeLinearLayout;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * 为摄像机配置连接Wifi
 * <p/>
 * Created by zhoushaopei on 15/6/18.
 */
public class ConfigWifiFragment extends BaseFragment implements ScanWifiFragment.OnWifiChecked, CompoundButton.OnCheckedChangeListener
            , TextWatcher, AdvancedConfigFragment.AdvancedIp, ResizeLinearLayout.OnResizeListener {

    @ViewInject(R.id.ssid)      TextView mWiFiSSID;
    @ViewInject(R.id.account_lay)       RelativeLayout mAccountLay;
    @ViewInject(R.id.select_lay)        RelativeLayout mSelectWifi;
    @ViewInject(R.id.pass_lay)          RelativeLayout mPassLay;
    @ViewInject(R.id.hide_or_show_btn)  TextView mPassBtn;
    @ViewInject(R.id.start_configurtion)LinearLayout mStartConfig;
    @ViewInject(R.id.wifi_password)     EditText mWifiPass;
    @ViewInject(R.id.account_text)      EditText mAccountText;
    @ViewInject(R.id.actionbar_title)   TextView mTitle;
    @ViewInject(R.id.actionbar_back)    ImageView mBack;
    @ViewInject(R.id.actionbar_finish)  TextView mCancel;
    @ViewInject(R.id.start_register)    ImageView mStartRegisterImg;
    @ViewInject(R.id.start_register_txt)TextView mStartRegisterTxt;
    @ViewInject(R.id.scroll_view)       ScrollView mScrollView;
    @ViewInject(R.id.key_bord)          ResizeLinearLayout mKeyBord;
    @ViewInject(R.id.wifi_lay)          LinearLayout   mWifiLay;

    private WifiInfoWrapper wifiInfoWrapper;
    CommonCommitDialog cusDialog;
    private String wifiSSID;
    private WifiManager wifiManager;
    private String ipText;
    private String netmaskText;
    private String gatewayTtext;
    private static final int BIGGER = 1;//键盘消失
    private static final int SMALLER= 2;//键盘弹出
    private int mSmallHeight;           //键盘高度
    private int mBigHeight;             //屏幕高度
    private int mBigWidth;                 //屏幕宽度
    private int wifiType = -1;
    private Activity activity;
    MediaPlayer mediaPlayer;
    private int current;
    private AudioManager mAudioManager;

    public Fragment actionInstance(FragmentActivity activity) {
        ConfigWifiFragment fragment = new ConfigWifiFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        this.activity = activity;
        return fragment;
    }

    @Override
    public void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.conn_dev_wifi_text)
        .setCommonBackgroud(R.color.setupdev_actionbar_bg)
        .setCommonFinish(R.string.advance_text)
        .setCommonFinishClick(new OnCommonClickListener() {
            @Override
            public void onClick(View v) {
                InputUtil.hideSoftInput(getActivity(), mWifiPass);
                mediaPlayer.stop();
                AdvancedConfigFragment advacedConfig = AdvancedConfigFragment.actionInstance();//, item
                advacedConfig.registerAdvance(ConfigWifiFragment.this);
                addToBackStack(advacedConfig);
            }
        });
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_wifi, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        initView();
        mBigHeight = getActivity().getResources().getDisplayMetrics().heightPixels;
        mBigWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
         mediaPlayer = MediaPlayer.create(getActivity(),R.raw.input_wifi);
        boolean isInput = ErmuBusiness.getPreferenceBusiness().getInputWifiPa();
        String language = LanguageUtil.getLanguage();
        if (isInput){
            mAudioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
            current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int count  = (max-1)/2+1;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, count, 0);
            if (language.equals("zh")) mediaPlayer.start();
            isInput = false;
            ErmuBusiness.getPreferenceBusiness().setInputWifiPa(isInput);
        }
        return view;
    }

    private void initView() {
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiInfoWrapper = new WifiInfoWrapper();
        mWifiPass.setTag(0);
        wifiSSID = ErmuBusiness.getSetupDevBusiness().scanConnectSSID();
        wifiType = ErmuBusiness.getSetupDevBusiness().getConnectWifiType();


        ErmuBusiness.getSetupDevBusiness().scanWifi();
        //ErmuBusiness.getSetupDevBusiness().scanConnectBSSID();
        boolean is5GHz = WifiNetworkManager.getInstance(getActivity()).is5GHz();
        if (is5GHz || !ErmuApplication.isConnected()) {
            wifiSSID = "";
            wifiType = WifiType.WEP;
            mWiFiSSID.setHint(R.string.select_wifi_txt);
        } else {
            if (wifiType != WifiType.OPEN) {
                getWrapPass();
            }
        }
        mWifiPass.addTextChangedListener(this);
        mKeyBord.setOnResizeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refreshView();
    }

    private void refreshView() {
        mWiFiSSID.setText(wifiSSID);
        if (wifiType == WifiType.OPEN) {
            mPassLay.setVisibility(View.GONE);
            mAccountLay.setVisibility(View.GONE);
            mAccountLay.setBackgroundResource(R.drawable.white_back);
        } else if(wifiType == WifiType.EAP) {
            mAccountLay.setVisibility(View.VISIBLE);
            mPassLay.setVisibility(View.VISIBLE);
            mAccountText.addTextChangedListener(this);
        } else {
            mAccountLay.setVisibility(View.GONE);
            mPassLay.setVisibility(View.VISIBLE);
        }
        mStartConfig.setEnabled(enableConfBtn());
    }


    private void getWrapPass() {
        com.iermu.client.model.WifiInfo wifiInfo = wifiInfoWrapper.queryWifiInfo(wifiSSID);
        if (wifiInfo != null) {
            String pass = wifiInfo.getPass();
            String account = wifiInfo.getAccount();
            mWifiPass.setText(pass);
            mAccountText.setText(account);
        } else {
            mWifiPass.setText("");
        }
    }

    //Wifi为null || (密码为null && 加密类型 ) || ((用户名为null || 密码为null ) && 802.1x )
    //-> (密码小于8位 && 加密类型) || (密码小于8位 && 802.1x)
    private boolean enableConfBtn() {
        String ssid     = mWiFiSSID.getText().toString();
        String pwd      = mWifiPass.getText().toString();
        String account  = mAccountText.getText().toString();
        boolean emptyWifi = TextUtils.isEmpty(ssid);
        boolean emptyPwd = TextUtils.isEmpty(pwd);
        boolean emptyAccount = TextUtils.isEmpty(account);

        boolean disable = emptyWifi
                || (emptyPwd && (wifiType!=WifiType.OPEN))
                || ((emptyAccount || emptyPwd) && (wifiType==WifiType.EAP))
                || (pwd.length()<8 && (wifiType!=WifiType.OPEN));
        return !disable;
    }

    @OnClick(value = {R.id.select_lay, R.id.hide_or_show_btn, R.id.start_configurtion})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_lay:
                mediaPlayer.stop();
                InputUtil.hideSoftInput(getActivity(), mWifiPass);
                ScanWifiFragment wifiFrag = ScanWifiFragment.actionInstance(getActivity());
                wifiFrag.registerWifiChecked(this);
                addToBackStack(wifiFrag);
                break;
            case R.id.hide_or_show_btn:
                boolean bShowPwd = Integer.parseInt(mWifiPass.getTag().toString()) == 1;
                mWifiPass.setInputType(bShowPwd ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mWifiPass.setTag(bShowPwd ? 0 : 1);
                mPassBtn.setText(bShowPwd ? getResources().getString(R.string.hide_pass) : getResources().getString(R.string.show_pass));
                String wifiPas = mWifiPass.getText().toString();
                mWifiPass.setSelection(wifiPas.length());
                break;
            case R.id.start_configurtion:
                InputUtil.hideSoftInput(getActivity(),mWifiPass);
                String wifiSSID = mWiFiSSID.getText().toString().trim();
                String wifiAccount = mAccountText.getText().toString();
                String wifiPass = mWifiPass.getText().toString();
                wifiInfoWrapper.insert(wifiSSID, wifiAccount, wifiPass);
                CamDevConf conf = new CamDevConf(wifiSSID);
                conf.setWifiType(wifiType);
                conf.setWifiAccount(wifiAccount);
                conf.setWifiPwd(wifiPass);
                conf.setDhcpIP(ipText);
                conf.setDhcpGateway(gatewayTtext);
                conf.setDhcpNetmask(netmaskText);
                mediaPlayer.stop();
                ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
                //CamDevConf camDevConf  = new CamDevConf(scanResult,wifiAccount,wifiPass, ipText,netmaskText,gatewayTtext);
//                WifiTransferModel wifiInfo = new WifiTransferModel(wifiSSID,wifiPass,wifiAccount,isAdvanceCon, ipText, netmaskText, gatewayTtext,wifiType);
//                    Fragment fragment = ConnDevFragment.actionInstance(itemCamDev, wifiSSID, wifiAccount, wifiPass, isAdvanceCon, ipText, netmaskText, gatewayTtext);
                Fragment fragment = ScanDevFragment.actionInstance(getActivity(), conf);
                super.addToBackStack(fragment);
//                isAdvanceCon = ErmuBusiness.getPreferenceBusiness().getAdvancedConfig();
//                int devType = itemCamDev.getDevType();
//                if (devType == CamDevType.TYPE_AP && isAdvanceCon) {    //TODO 先注册服务器,成功1、手动配置页面2、停止动画（成功或者失败）
//                    animation();
//                    ErmuBusiness.getSetupDevBusiness().addSetupDevListener(mSetupListener);
//                    ErmuBusiness.getSetupDevBusiness().registerCamStep(itemCamDev);
//                    //ErmuBusiness.getSetupDevBusiness()._registerCam(itemCamDev);
//                } else {
//                    WifiTransferModel wifiInfo = new WifiTransferModel(wifiSSID,wifiPass,wifiAccount,isAdvanceCon, ipText, netmaskText, gatewayTtext);
////                    Fragment fragment = ConnDevFragment.actionInstance(itemCamDev, wifiSSID, wifiAccount, wifiPass, isAdvanceCon, ipText, netmaskText, gatewayTtext);
//                    Fragment fragment = new ScanDevFragment().actionInstance(getActivity(),wifiInfo);
//                    super.addToBackStack(fragment);
//                }
                break;
        }
    }

    @Override
    public void callback(ScanResult result) {
        if (result == null) return;
        this.wifiSSID = result.SSID;
        this.wifiType = CmsUtil.getWifiType(result.capabilities);
        if (wifiType != WifiType.OPEN) getWrapPass();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.manual_mode:
                cusDialog = new CommonCommitDialog(getActivity());
                cusDialog.show();
                cusDialog.setStatusText(getResources().getString(R.string.push));
                break;
            case R.id.ip_mode:
                cusDialog = new CommonCommitDialog(getActivity());
                cusDialog.show();
                cusDialog.setStatusText(getResources().getString(R.string.push));
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mStartConfig.setEnabled(enableConfBtn());
    }

    @Override
    public void afterTextChanged(Editable s) {
//        Pattern p = Pattern.compile("[0-9]*");
//        Matcher m ;
//        p=Pattern.compile("[\u4e00-\u9fa5]");
//        m=p.matcher(s);
//        if(m.matches()){
//            Toast.makeText(getActivity(), "输入的是汉字", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void ipconfig(String ip, String netmask, String gareway) {
        ipText = ip;
        netmaskText = netmask;
        gatewayTtext = gareway;
    }

    @Override
    public void onPause() {
        super.onPause();
        InputUtil.hideSoftInput(getActivity(), mWifiPass);
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        mediaPlayer.stop();
        ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
    }

    @Override
    public void onResize(int w, int h, int oldw, int oldh) {
        Message msg = new Message();
        if(h < oldh){
            mSmallHeight = h;
            msg.what = SMALLER;
        }else{
            if(oldh == 0){
                mBigHeight = h;
            }
            if(h >= mBigHeight){
                msg.what = BIGGER;
            }
        }
        mHandler.sendMessage(msg);
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case SMALLER:
                    mWifiLay.setVisibility(View.GONE);
                    mScrollView.scrollTo(mBigWidth, mSmallHeight);
                    break;
                case BIGGER:
                    mWifiLay.setVisibility(View.VISIBLE);
                    mScrollView.scrollTo(0, 0);
                    break;
            }
        }
    };
}