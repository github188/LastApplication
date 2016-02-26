package com.iermu.ui.fragment.setupdev;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.business.impl.setupdev.setup.SetupStatus;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.listener.OnUpdateCamNameListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.view.ConnCamDialog;
import com.iermu.ui.view.DimenUtils;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.Serializable;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * 开始配置(连接)设备
 * <p/>
 * Created by zhoushaopei on 15/6/18.
 */
public class ConnDevFragment extends BaseFragment implements OnUpdateCamNameListener {

    private static final String KEY_CAMDEV     = "camdev";
    private static final String KEY_CAMDEVCONF = "camDevConf";

    @ViewInject(R.id.schedule_num)      TextView mScheduleNum;
    @ViewInject(R.id.schedule_img)      ImageView mScheduleImg;
    @ViewInject(R.id.conn_container)    LinearLayout mConnContainer;
    @ViewInject(R.id.actionbar_title)   TextView mTitle;
    @ViewInject(R.id.actionbar_back)    ImageView mBack;
    @ViewInject(R.id.actionbar_finish)  TextView mActionBarFinish;

    private Animation animation;
    private Animation animation1;
    private AlertDialog mUpdateCamDialog;
    private ConnCamDialog mNameDialog;
    private String deviceId;
    private SweetAlertDialog dialog;

    /**
     * 获取 开始配置(连接)设备 实例
     *
     * @param item
     * @return
     */
    public static Fragment actionInstance(CamDev item, CamDevConf camDevConf) {//
        ConnDevFragment fragment = new ConnDevFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CAMDEV, item);
        bundle.putSerializable(KEY_CAMDEVCONF, camDevConf);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_about);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        mTitle.setText(getResources().getString(R.string.conn_dev_text));
        mBack.setVisibility(View.INVISIBLE);
        mActionBarFinish.setText(getResources().getString(R.string.cancle_txt));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_devices, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        Bundle bundle = getArguments();
        CamDev camDev   = (CamDev) bundle.getSerializable(KEY_CAMDEV);
        CamDevConf conf = (CamDevConf) bundle.getSerializable(KEY_CAMDEVCONF);

        deviceId = camDev.getDevID();
        animation();
//        handler.postDelayed(runnable, TIME);
        boolean manualMode = ErmuBusiness.getPreferenceBusiness().getAdvancedConfig();
        ErmuBusiness.getCamSettingBusiness().registerListener(OnUpdateCamNameListener.class, this);
        ISetupDevBusiness business = ErmuBusiness.getSetupDevBusiness();
        business.addSetupDevListener(mListener);
        business.connectCam(camDev, manualMode, conf);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ISetupDevBusiness business = ErmuBusiness.getSetupDevBusiness();
        business.quitSetupDev();
        business.removeSetupDevListener(mListener);
        ErmuBusiness.getCamSettingBusiness().unRegisterListener(OnUpdateCamNameListener.class, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dialog != null && dialog.isShowing()) return true;
                quitDevDialog();
        }
        return true;
    }


    @OnClick(value = {R.id.actionbar_finish})
    private void onClick(View view) {
        switch (view.getId()) {
//            case R.id.back_home:
//                ErmuBusiness.getSetupDevBusiness().quitSetupDev();
//                popBackAllStack();
//                break;
            case R.id.actionbar_finish:
                quitDevDialog();
                break;

        }
    }

    private void quitDevDialog() {
        dialog = new SweetAlertDialog(getActivity());
        String quitDev = getActivity().getResources().getString(R.string.quit_add_dev);
        String quitContent = getActivity().getResources().getString(R.string.quit_add_content);
        String quitConfirm = getActivity().getResources().getString(R.string.quit_add_confrim);
        String quitCancel = getActivity().getResources().getString(R.string.quit_cancle);
        dialog.setTitleText(quitDev);
        dialog.setContentText(quitContent);
        dialog.setConfirmText(quitConfirm);
        dialog.setCancelText(quitCancel);
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                popBackAllStack();
                ErmuBusiness.getSetupDevBusiness().quitSetupDev();
            }
        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        animation();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ((animation != null && animation.hasStarted()) || (animation1 != null && animation1.hasStarted())) {
            //mLoadingImg.clearAnimation();
            //mLoadingImg.startAnimation(animation1);
            mScheduleImg.clearAnimation();
            mScheduleImg.startAnimation(animation);
        }
    }

    private void stopAnimation() {
        if (animation != null && animation.hasStarted()) {
            mScheduleImg.clearAnimation();
            //mLoadingImg.clearAnimation();
        }
    }

    private void animation() {
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_scans);
        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_scans);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation1.setInterpolator(lin);
        if (animation != null || animation1 != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScheduleImg.startAnimation(animation);
                    //mLoadingImg.startAnimation(animation1);
                }
            }, 100);
        }
    }

    View parentView;
    TextView textView;
    ImageView loadingView;
    Fragment fragment;
    //配置设备监听器
    OnSetupDevListener mListener = new OnSetupDevListener() {
        @Override
        public void onSetupStatus(SetupStatus status) {
            super.onSetupStatus(status);
            switch (status) {
                case SETUP_INITNG:
                    parentView = View.inflate(getActivity(), R.layout.item_conndev_tipmsg, null);
                    textView = (TextView) parentView.findViewById(R.id.loading_txt);
                    loadingView = (ImageView) parentView.findViewById(R.id.loading_img);
                    loadingView.startAnimation(animation1);
                    textView.setText(getResources().getString(R.string.current_conn_cloud));
                    mConnContainer.addView(parentView, 0);
                    break;
                case CHECK_ENV_HIWIFI_CONNEVCT_TIMEOUT:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt0 = getResources().getString(R.string.hiwifi_connect_timeout);
                    textView.setText(txt0);

                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt0);
                    addToBackStack(fragment);
                    break;
                case SETUP_INITED:
                    //
                    break;
                //注册设备
                case REGISTER_SUCCESS:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_finish);
                    textView.setText(getResources().getString(R.string.register_cloud_success));
                    break;
                case REGISTED:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_finish);
                    textView.setText(getResources().getString(R.string.registed_cloud));
                    break;
                case REGISTER_FAIL:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String str = getResources().getString(R.string.register_cloud_fail);
                    textView.setText(str);
                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), str);
                    addToBackStack(fragment);
                    break;
                case REGISTER_NOTPERMISSION:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt = getResources().getString(R.string.registed_device);
                    textView.setText(txt);

                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt);
                    addToBackStack(fragment);
                    break;
                case CONNECT_DEV_ING://case CONF_DEV_ING:
                    parentView = View.inflate(getActivity(), R.layout.item_conndev_tipmsg, null);
                    textView = (TextView) parentView.findViewById(R.id.loading_txt);
                    loadingView = (ImageView) parentView.findViewById(R.id.loading_img);
                    loadingView.startAnimation(animation1);
                    textView.setText(getResources().getString(R.string.current_config_parameter));
                    mConnContainer.addView(parentView, 0);
                    YoYo.with(Techniques.BounceInDown).duration(1000).playOn(textView);
                    break;
                //配置设备参数
                case CONF_DEV_SUCCESS:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_finish);
                    textView.setText(getResources().getString(R.string.config_parameter_success));
                    break;
                case CONF_CONNECTDEV_FAIL:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt1 = getResources().getString(R.string.config_parameter_fail);
                    textView.setText(txt1);

                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt1);
                    addToBackStack(fragment);
                    break;
                case CONF_PERMISSION_DENIED:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt2 = getResources().getString(R.string.no_permission);
                    textView.setText(txt2);
                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt2);
                    addToBackStack(fragment);
                    break;
                case CONF_DEV_FAIL:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt3 = getResources().getString(R.string.config_parameter_try);
                    textView.setText(txt3);
                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt3);
                    addToBackStack(fragment);
                    break;
                //结果
                case CONNECT_WIFI_ING:
                    parentView = View.inflate(getActivity(), R.layout.item_conndev_tipmsg, null);
                    textView = (TextView) parentView.findViewById(R.id.loading_txt);
                    loadingView = (ImageView) parentView.findViewById(R.id.loading_img);
                    loadingView.startAnimation(animation1);
                    textView.setText(getResources().getString(R.string.check_online));
                    mConnContainer.addView(parentView, 0);
                    YoYo.with(Techniques.BounceInDown).duration(1000).playOn(textView);
                    break;
                case SETUP_SUCCESS:
                    loadingView.clearAnimation();
                    if (mNameDialog == null)
                    if (mNameDialog == null || (!dialog.isShowing())) showAcceptAuth();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();

                    loadingView.setBackgroundResource(R.drawable.add_icon_finish);
                    textView.setText(getResources().getString(R.string.setup_success));
                    break;
                case SETUP_FAIL:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt4 = getResources().getString(R.string.setup_fail);
                    textView.setText(txt4);
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt4);
                    addToBackStack(fragment);
                    break;
                //其他出错情况
                case CONNECT_DEV_FAIL:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt7 = getResources().getString(R.string.connect_dev_fail);
                    textView.setText(txt7);
                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt7);
                    addToBackStack(fragment);
                    break;
                case CONNECT_WIFI_FAIL:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt8 = getResources().getString(R.string.connect_wifi_fail);
                    textView.setText(txt8);
                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt8);
                    addToBackStack(fragment);
                    break;
                case GET_HIWIFI_IP_FAIL:
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_tips);
                    String txt9 = getResources().getString(R.string.get_hiwifi_ip_fail);
                    textView.setText(txt9);
                    stopAnimation();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    fragment = ConnDevErrFragment.actionInstance(getActivity(), txt9);
                    addToBackStack(fragment);
                    break;
            }
        }

        @Override
        public void onUpdateProgress(int progress) {
            super.onUpdateProgress(progress);
            mScheduleNum.setText(Integer.toString(progress));
            if (progress == 100) {
                if (loadingView != null) {
                    loadingView.clearAnimation();
                    loadingView.setBackgroundResource(R.drawable.add_icon_finish);
                }
                stopAnimation();
            }
        }
    };

    private void showAcceptAuth() {
        String camDescription = ErmuBusiness.getMimeCamBusiness().getCamDescription(deviceId);
        mNameDialog = new ConnCamDialog(getActivity(), camDescription);
        mNameDialog.setClicklistener(new ConnCamDialog.ClickListenerInterface() {
            @Override
            public void doCheck(String name) {
                if (TextUtils.isEmpty(name)) name = getActivity().getResources().getString(R.string.devices_name);
                ErmuBusiness.getCamSettingBusiness().updateCamName(deviceId, name);
                if (mNameDialog != null) mNameDialog.dismiss();
                popBackAllStack();
            }
        });
        mNameDialog.show();
    }

    private void updateNameDialog() {
        String camDescription = ErmuBusiness.getMimeCamBusiness().getCamDescription(deviceId);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.update_dev_name, null);
        RelativeLayout mainWin = (RelativeLayout) view.findViewById(R.id.layout_main_win);
        final EditText devName = (EditText) view.findViewById(R.id.dev_name);
        if (!TextUtils.isEmpty(camDescription)) devName.setText(camDescription);
        Button okBtn = (Button) view.findViewById(R.id.btn_see_dev);

        mainWin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               InputUtil.hideSoftInput(getActivity(),devName);
                return true;
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = devName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) name = getActivity().getResources().getString(R.string.devices_name);
                ErmuBusiness.getCamSettingBusiness().updateCamName(deviceId, name);
                if (mUpdateCamDialog != null) mUpdateCamDialog.dismiss();
                popBackAllStack();
            }
        });
        builder.setView(view);
        mUpdateCamDialog = builder.create();
        mUpdateCamDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode  == KeyEvent.KEYCODE_BACK) {
                    mUpdateCamDialog.dismiss();
                    popBackAllStack();
                    return true;
                }
                return false;
            }
        });
        mUpdateCamDialog.setCancelable(false);
        WindowManager.LayoutParams params = mUpdateCamDialog.getWindow().getAttributes();
        params.width = 840;
        mUpdateCamDialog.getWindow().setAttributes(params);
        mUpdateCamDialog.show();
    }

    @Override
    public void onUpdateCamName(Business business) {
        if (mUpdateCamDialog != null) mUpdateCamDialog.dismiss();
        if (business.getCode() == BusinessCode.SUCCESS) {
//            ErmuApplication.toast("摄像机名称设置成功");
        } else {
            ErmuApplication.toast(getResources().getString(R.string.update_description));
        }
    }
}
