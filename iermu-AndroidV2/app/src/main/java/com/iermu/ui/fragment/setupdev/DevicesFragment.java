package com.iermu.ui.fragment.setupdev;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.business.impl.setupdev.setup.SetupStatus;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.ViewPagerZoom.FixedScroller;
import com.iermu.ui.view.ViewPagerZoom.ZoomOutSlideTransformer;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * 摄像机添列表
 *
 * Created by zhoushaopei on 15/6/18.
 */
public class DevicesFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private static final String KEY_CAMDEVCONF  = "camdevconf";

    @ViewInject(R.id.devices_num)       LinearLayout mDevNumTv;
    @ViewInject(R.id.num)               TextSwitcher textSwitcher;
    @ViewInject(R.id.view_pager)        ViewPager mViewPager;
    @ViewInject(R.id.viewpager_lay)     RelativeLayout mViewPagerLay;
    @ViewInject(R.id.find_help)         Button mFindHelp;
    @ViewInject(R.id.indicator)         CirclePageIndicator indicator;
    @ViewInject(R.id.start_register)    ImageView mStartRegisterImg;
    @ViewInject(R.id.conn_dev)          Button mStartConnButton;

    private final static int TIMER = 10000;
    private Field mScroller = null;
    private List<CamDev> devList = new ArrayList<CamDev>();
    private ViewPagerAdapter mAdapter;
    private int mPosition;
    private Animation animation;
//    private AlertDialog dialog;

    private CamDevConf camDevConf;
    int viewWidth;
    int viewHeight;
    /**
     * 启动摄像机添加向导页面
     * @return
     */
    public static Fragment actionInstance(CamDevConf camDevConf) {
        DevicesFragment fragment = new DevicesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CAMDEVCONF, camDevConf);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_add_camera);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        Bundle bundle = getArguments();
        camDevConf = (CamDevConf) bundle.getSerializable(KEY_CAMDEVCONF);
//        final ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
//        devBusiness.addSetupDevListener(mListener);
        ErmuBusiness.getSetupDevBusiness().addSetupDevListener(mSetupListener);
        initViewPager();
//       RelativeLayout.LayoutParams  paramsParent=  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
//        paramsParent.setMargins(150,50,150,350);
//        mViewPager.setLayoutParams(paramsParent);
        mFindHelp.setText(Html.fromHtml("<u>" + getActivity().getResources().getString(R.string.how_find_dev) + "</u>"));
        YoYo.with(Techniques.BounceInDown).duration(500).playOn(mDevNumTv);
        return view;
    }


    private void initViewPager() {
        mViewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        mViewPager.setPageMargin(80);

        mViewPager.setOnPageChangeListener(this);
        mAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        //mViewPager.setCurrentItem(0);//(mImageViews.length) * 100
        indicator.setViewPager(mViewPager);
        indicator.setOnPageChangeListener(this);

        mViewPagerLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });

        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
            FixedScroller scroller = new FixedScroller(mViewPager.getContext(), sInterpolator);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @OnClick(value = {R.id.cancel,R.id.find_help, R.id.conn_dev, R.id.actionbar_back})
    public void onClick(View v) {
        ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
        switch (v.getId()) {
            case R.id.cancel:
                popBackAllStack();
                devBusiness.quitScanCam();
                break;
            case R.id.actionbar_back:
                popBackStack();
                break;
            case R.id.find_help:
                Fragment fragment = FindDevNumFragment.actionInstance(getActivity());
                addToBackStack(fragment);
                break;
            case R.id.conn_dev:
                if (camDevsMap != null) {
                    CardItemFragment item = (CardItemFragment) mAdapter.getItem(mPosition);
                    String deviceId = item.getDeviceId().toString().trim();
//                    boolean b = camDevsMap.containsKey(deviceId);
                    CamDev camDev = camDevsMap.get(deviceId);

                    boolean isAdvanceCon = ErmuBusiness.getPreferenceBusiness().getAdvancedConfig();
//                    int devType = camDev.getDevType();
                    ErmuBusiness.getSetupDevBusiness().checkCamEnvironment(camDev, isAdvanceCon, camDevConf);
//                    String wifiSSID = ErmuBusiness.getSetupDevBusiness().scanConnectSSID();
//                    if(devType == CamDevType.TYPE_SMART && !wifiSSID.equals(camDevConf.getWifiSSID())) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        View view = View.inflate(getActivity(), R.layout.conn_smart_wifi_notice, null);
//                        Button mKnowledge = (Button) view.findViewById(R.id.knowledge_btn);
//                        mKnowledge.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                                popBackStack(getActivity(), ConfigWifiFragment.class);
//                            }
//                        });
//                        builder.setView(view);
//                        dialog = builder.create();
//                        dialog.show();
//                    } else if (devType == CamDevType.TYPE_AP && isAdvanceCon) {    //TODO 先注册服务器,成功1、手动配置页面2、停止动画（成功或者失败）
//                        animation();
//                        ErmuBusiness.getSetupDevBusiness().addSetupDevListener(mSetupListener);
//                        ErmuBusiness.getSetupDevBusiness().registerCamStep(camDev);
//                        //ErmuBusiness.getSetupDevBusiness()._registerCam(itemCamDev);
//                    } else {
//                        Fragment wifiFragment = ConnDevFragment.actionInstance(camDev,camDevConf);
//                        super.addToBackStack(wifiFragment);
//                    }

//                    Fragment wifiFragment = ConfigWifiFragment.actionInstance(getActivity(), camDev);
//                    Fragment wifiFragment = ConnDevFragment.actionInstance(camDev,wifiInfo);
//                    super.addToBackStack(wifiFragment);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
        //devBusiness.removeSetupDevListener(mListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
            devBusiness.addSetupDevListener(mSetupListener);
            devBusiness.scanCam(camDevConf);
            refreshView();
        } else {
            //ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
            //devBusiness.quitScanCam();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            popBackStack();
        }
        return true;
    }

    Map<String, Fragment> fragmentMap = new LinkedHashMap<String, Fragment>();
    Map<String, CamDev> camDevsMap = new HashMap<String, CamDev>();
    private void refreshView() {
        devList = ErmuBusiness.getSetupDevBusiness().getScanCamDev();
        if (devList.size() > 0) {
            for (int i=0;i<devList.size();i++) {
                CamDev camDev = devList.get(i);
                String devID = camDev.getDevID();
                camDevsMap.put(devID,camDev);
                Fragment fragment = CardItemFragment.actionInstance(getActivity(), devID);
                if( !fragmentMap.containsKey(devID) ) {
                    fragmentMap.put(devID, fragment);
                }
            }
            Collection<Fragment> values = fragmentMap.values();
            ArrayList<Fragment> fragments = new ArrayList<Fragment>(values);
            mAdapter.setFragments(fragments);
        }
        int count = mAdapter.getCount();
        if(fragmentMap.size() == count) {
            textSwitcher.setCurrentText(" " + String.valueOf(count) + " ");
        } else {
            textSwitcher.setText(" " + String.valueOf(count) + " ");
        }
    }

//    OnSetupDevListener mListener = new OnSetupDevListener() {
//        @Override
//        public void onScanCamList(List<CamDev> list) {
//            super.onScanCamList(list);
//            refreshView();
//        }
//
//        @Override
//        public void onScanFail(int businessCode, String message) {
//            super.onScanFail(businessCode, message);
//            switch(businessCode){
//            case BusinessCode.WIFI_CLOSE:
//                break;
//            }
//        }
//    };

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int position) {
//        indicator.setCurrentItem(position);
        mPosition = position;
        for(int i=0; i<mAdapter.getCount(); i++){
            CardItemFragment item = (CardItemFragment) mAdapter.getItem(i);
            if(i == position){
                item.setSelectDevBg();
            }else{
                item.setSelectDevNormal();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> fragments = new ArrayList<Fragment>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void setFragments(List<Fragment> frag) {
            this.fragments = frag;
            notifyDataSetChanged();
        }
    }

    SweetAlertDialog dialog;
    //配置设备监听器
    OnSetupDevListener mSetupListener = new OnSetupDevListener() {
        @Override
        public void onScanCamList(List<CamDev> list) {
            super.onScanCamList(list);
            refreshView();
        }

        @Override
        public void onSetupStatus(SetupStatus status) {
            super.onSetupStatus(status);
            switch(status){
            case CHECK_ENV_OK:
                CardItemFragment item1 = (CardItemFragment) mAdapter.getItem(mPosition);
                String deviceId1 = item1.getDeviceId();
                CamDev camDev1 = camDevsMap.get(deviceId1);
                Fragment wifiFragment = ConnDevFragment.actionInstance(camDev1, camDevConf);
                addToBackStack(wifiFragment);
                break;
            case CHECK_ENV_SMART_TIMEOUT:
                Fragment fragment0 = ConnDevErrFragment.actionInstance(getActivity(), getString(R.string.smart_timeout));
                addToBackStack(fragment0);
//                dialog = new SweetAlertDialog(getActivity());
//                dialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
//                dialog.setTitleText(getResources().getString(R.string.check_cam_environment));
//                dialog.setContentText(getResources().getString(R.string.smart_timeout));
//                dialog.setCancelText(getResources().getString(R.string.cancle_txt));
//                dialog.setConfirmText(getResources().getString(R.string.rescancam_txt));
//                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog alert) {
//                        alert.dismiss();
//                        popBackStack(getActivity(), ConfigWifiFragment.class);
//                    }
//                });
//                dialog.show();
                break;
            case CHECK_ENV_SMART_WIFI_NOMATCH:
                Fragment fragment4 = ConnDevErrFragment.actionInstance(getActivity(), getString(R.string.smart_wifi_nomatch));
                addToBackStack(fragment4);
//                dialog = new SweetAlertDialog(getActivity());
//                dialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
//                dialog.setTitleText(getResources().getString(R.string.check_cam_environment));
//                dialog.setContentText(getResources().getString(R.string.smart_wifi_nomatch));
//                dialog.setCancelText(getResources().getString(R.string.cancle_txt));
//                dialog.setConfirmText(getResources().getString(R.string.rescancam_txt));
//                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog alert) {
//                        alert.dismiss();
//                        popBackStack(getActivity(), ConfigWifiFragment.class);
//                    }
//                });
//                dialog.show();
                break;
            case REGISTER_ING:
                animation();
                break;
            case REGISTER_NOTPERMISSION:
                Fragment fragment1 = ConnDevErrFragment.actionInstance(getActivity(),getResources().getString(R.string.cam_already_register));
                addToBackStack(fragment1);
                break;
            case REGISTER_SUCCESS:
            case REGISTED:
                CardItemFragment fragment2 = (CardItemFragment) mAdapter.getItem(mPosition);
                String deviceId2 = fragment2.getDeviceId();
                CamDev item2 = camDevsMap.get(deviceId2);
                Fragment configFragment2 = ManualConfigFragment.actionInstance(item2, camDevConf);
                addToBackStack(configFragment2);
                break;
            case REGISTER_FAIL:
                Fragment fragment3 = ConnDevErrFragment.actionInstance(getActivity(), getResources().getString(R.string.register_cloud_server_error));
                addToBackStack(fragment3);
                break;
            }
            stopAnimation();
        }
    };

    private void animation() {
        mStartRegisterImg.setVisibility(View.VISIBLE);
        mStartConnButton.setText(R.string.register_txt);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_scans);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        if (animation != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mStartRegisterImg.startAnimation(animation);
                }
            }, 100);
        }
    }

    private void stopAnimation() {
        if (animation != null && animation.hasStarted()) {
            mStartRegisterImg.setVisibility(View.GONE);
            mStartConnButton.setText(R.string.start_config_txt);
            mStartRegisterImg.clearAnimation();
        }
    }
}
