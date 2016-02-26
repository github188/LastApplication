package com.iermu.ui.fragment.setupdev;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描设备页面
 * <p/>
 * Created by zhoushaopei on 15/6/18.
 */
public class ScanDevFragment extends BaseFragment {

    private static final String KEY_CAMDEVCONF = "camDevConf";

    @ViewInject(R.id.actionbar_title)   TextView mTitle;
    @ViewInject(R.id.actionbar_back)    ImageView mBack;
    @ViewInject(R.id.actionbar_finish)  TextView mFinish;

    private Animation animation;
    private ImageView mIermuImg;
    private AlertDialog dialog;
    private List<CamDev> mCamDev;
    private int startTime;
    private int endTime;
    private final static int DISTANCE_SHORT = 5000;
    private final static int DISTANCE_LONG = 25000;
    Handler mHandler = new Handler();

    private CamDevConf camDevConf;

    public static Fragment actionInstance(Context context, CamDevConf camDevConf) {
        ScanDevFragment fragment = new ScanDevFragment();
        Bundle bundle = new Bundle();
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
        //TODO 临时解决
        mTitle.setText(getResources().getString(R.string.add_guide));
        mFinish.setText(getResources().getString(R.string.cancle_txt));
        mFinish.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scans_devices, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮
//        if (!Util.isNetworkConn(getActivity())){
//            Toast.makeText(getActivity(),"当前网络不出去WIFI",Toast.LENGTH_SHORT).show();
//        }
        Bundle bundle = getArguments();
        camDevConf = (CamDevConf) bundle.getSerializable(KEY_CAMDEVCONF);
        mIermuImg = (ImageView) view.findViewById(R.id.iermu_img);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_scans);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        if (animation != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIermuImg.startAnimation(animation);
                }
            }, 100);
        }

        mCamDev = new ArrayList<CamDev>();
        ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
        devBusiness.addSetupDevListener(mListener);
        devBusiness.scanCam(camDevConf);
        startTime = (int) (System.currentTimeMillis());
        return view;
    }

    @OnClick(value = {R.id.actionbar_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_back:
            popBackStack();
            break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
            devBusiness.addSetupDevListener(mListener);
            devBusiness.scanCam(camDevConf);
            startTime = (int) (System.currentTimeMillis());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mHandler!=null) mHandler.removeCallbacks(customRunnable);
        ErmuBusiness.getSetupDevBusiness().quitScanCam();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            popBackStack();
        }
        return true;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.conn_device_help, null);
        Button mKnowledge = (Button) view.findViewById(R.id.knowledge_btn);
        mKnowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                popBackStack(getActivity(), ConnAndBuyFragment.class);
                ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
                devBusiness.quitScanCam();
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        isAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (animation != null && mIermuImg != null && animation.hasStarted()) {
            mIermuImg.clearAnimation();
        }
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isAnimation();
    }

    private void isAnimation() {
        if (animation != null && mIermuImg != null && animation.hasStarted()) {
            mIermuImg.clearAnimation();
            mIermuImg.startAnimation(animation);
        }
    }

    Runnable customRunnable = new Runnable() {
        @Override
        public void run() {
            Fragment fragment = DevicesFragment.actionInstance(camDevConf);
//            Fragment fragment = FindNoDevFragment.actionInstance();
            addToBackStack(fragment);
        }
    };

    OnSetupDevListener mListener = new OnSetupDevListener() {
        @Override
        public void onScanCamList(List<CamDev> list) {
            super.onScanCamList(list);
            mCamDev = list;
            endTime = (int) (System.currentTimeMillis());
            if (mCamDev.size() > 0) {
                int time = endTime - startTime;
                ErmuBusiness.getSetupDevBusiness().removeSetupDevListener(mListener);
//                boolean b = ErmuBusiness.getSetupDevBusiness().existedSmartCamDev();
//                if(b) {
                    long delayMillis = (time >= DISTANCE_SHORT) ? 0 : DISTANCE_SHORT-time;
                    mHandler.postDelayed(customRunnable, delayMillis);
//                } else {
//                    long delayMillis = (time >= DISTANCE_LONG) ? 0 : DISTANCE_LONG-time;
//                    mHandler.postDelayed(customRunnable, delayMillis);
//                }
            }
        }

        @Override
        public void onScanFail(int businessCode, String message) {
            super.onScanFail(businessCode, message);
            endTime = (int) (System.currentTimeMillis());
            switch (businessCode) {
                case BusinessCode.WIFI_CLOSE:
                    ErmuApplication.toast(getString(R.string.current_wifi_state));
                    break;
                case BusinessCode.NOTFIND_SCANDEV:
                    Fragment fragment = FindNoDevFragment.actionInstance();
                    addToBackStack(fragment);
//                    showDialog();
                    break;
            }
        }
    };
}
