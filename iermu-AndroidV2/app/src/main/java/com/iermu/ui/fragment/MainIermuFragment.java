package com.iermu.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emptylayout.EmptyLayout;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.listener.OnCheckUpgradeVersionListener;
import com.iermu.client.listener.OnDropGrantDeviceListener;
import com.iermu.client.listener.OnGetCamUpdateStatusListener;
import com.iermu.client.listener.OnMimeCamChangedErrorListener;
import com.iermu.client.listener.OnMimeCamChangedListener;
import com.iermu.client.listener.OnStoreSuccessListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.UpgradeVersion;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.viewmodel.CamUpdateStatus;
import com.iermu.client.model.viewmodel.MimeCamItem;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.activity.MainActivity;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.adapter.MainIermuAdapter;
import com.iermu.ui.fragment.MineIermu.MineLiveFragment;
import com.iermu.ui.fragment.setupdev.ConnAndBuyFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonDialog;
import com.iermu.ui.view.LoadingView;
import com.iermu.ui.view.UpgradeTipDialog;
import com.iermu.ui.view.UpgradeViewloading;
import com.lib.pulltorefreshview.PullToRefreshLayout;
import com.lib.pulltorefreshview.pullableview.PullableListView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * 我的爱耳目页面
 * <p/>
 * Created by wcy on 15/6/18.
 */
public class MainIermuFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        PullToRefreshLayout.OnRefreshListener, OnMimeCamChangedListener, AbsListView.OnScrollListener
        , OnDropGrantDeviceListener, MainIermuAdapter.OnDropCallBack, OnMimeCamChangedErrorListener, OnStoreSuccessListener,OnCheckUpgradeVersionListener,OnGetCamUpdateStatusListener {

    @ViewInject(R.id.vedio_list)
    PullableListView mPullableListView;
    @ViewInject(R.id.lvPullLayout)
    PullToRefreshLayout mPullableLayout;
    @ViewInject(R.id.load_view)
    LoadingView loadView;
    @ViewInject(R.id.emptyView_)
    LinearLayout MemptyView;
    @ViewInject(R.id.add_cam)
    Button addCam;
    @ViewInject(R.id.day_time)
    TextView mDayTime;
    @ViewInject(R.id.img_bg)
    ImageView mImgBg;
    @ViewInject(R.id.iermu_bg)
    LinearLayout mIermuBg;
    @ViewInject(R.id.bg_img)
    ImageView mBgImg;
    @ViewInject(R.id.public_channel)
    TextView mPublicChannel;
    @ViewInject(R.id.maintab_public)
    RadioButton mMaintabPublick;
    @ViewInject(R.id.net_error)
    RelativeLayout mNetError;
    @ViewInject(R.id.error_btn)
    TextView mErrorBtn;
    @ViewInject(R.id.state_tv)
    TextView stateTv;
    @ViewInject(R.id.viewError)
    View viewError;
    @ViewInject(R.id.textViewError)
    TextView textViewError;
    @ViewInject(R.id.viewLoadBottom)
    View viewLoadBottom;
    @ViewInject(R.id.imageViewRefreshBottom)
    ImageView imageViewRefreshBottom;

    private EmptyLayout emptyLayout;
    private MainIermuAdapter adapter;
    private IMimeCamBusiness business;
    private int scrollState = 0;
    Animation an;
    private boolean animateFirst;
    private SweetAlertDialog dialog;
    private static MainActivity activity;
    private boolean isHidden = false;

    private ICamSettingBusiness camSettingBusiness;

    private int upgradePosition;

    UpgradeTipDialog upgDialog;

    public static boolean updateFlag = false;

    boolean upgDialogFlad = false;

    public static Map upgradeCountMap = new HashMap();
    public static Map upgradeStatusMap = new HashMap();

    private Timer mTimer;
    private TimerTask mTimerTask;
    private int count=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        if(upgDialog!=null)upgDialog.dismiss();
        super.onStart();
    }

    @Override
    public void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_mainiermu);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    public static Fragment actionInstance(Activity ctx) {
        activity = (MainActivity) ctx;
        return new MainIermuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainiermu, container, false);
        ViewHelper.inject(this, view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadView.startAnimation();
            }
        }, 100);

        business = ErmuBusiness.getMimeCamBusiness();
        camSettingBusiness = ErmuBusiness.getCamSettingBusiness();
        adapter = new MainIermuAdapter(getActivity(), this);
        mPullableListView.setOnScrollListener(this);
        mPullableListView.setPullUp(true);
        mPullableListView.setAdapter(adapter);
        mPullableListView.setOnItemClickListener(this);
        mPullableLayout.setOnRefreshListener(this);
        RotateAnimation refreshingAnimation =
                (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), com.lib.pulltorefreshview.R.anim.rotating);
        imageViewRefreshBottom.startAnimation(refreshingAnimation);

        ErmuBusiness.getPubCamCommentBusiness().registerListener(OnStoreSuccessListener.class, this);
        business.registerListener(OnMimeCamChangedListener.class, this);
        business.registerListener(OnMimeCamChangedErrorListener.class, this);
        business.registerListener(OnDropGrantDeviceListener.class, this);
        camSettingBusiness.registerListener(OnCheckUpgradeVersionListener.class,this);
        camSettingBusiness.registerListener(OnGetCamUpdateStatusListener.class,this);
        updateFlag = false;
        business.syncNewCamList();
        refreshAdapter();
        if (adapter.getCount() <= 0) {
            loadView.startAnimation();
        } else {
            mPullableListView.setEmptyView(MemptyView);
        }
        loadView.setVisibility((adapter.getCount() <= 0) ? View.VISIBLE : View.GONE);
        boolean visible = (loadView.getVisibility() == View.VISIBLE || adapter.getCount() > 0);
        if (visible) {
            showActionBar();
        } else {
            hideActionBar();
        }
        MemptyView.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        mPullableListView.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.empty_view_);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                Animation an = AnimationUtils.loadAnimation(getActivity(), R.anim.empty_view_);
                an.setAnimationListener(this);
                mImgBg.setAnimation(null);
                if (animateFirst) {
                    mImgBg.setVisibility(View.VISIBLE);
                    mBgImg.setVisibility(View.INVISIBLE);
                    mImgBg.startAnimation(an);
                } else {
                    mImgBg.setVisibility(View.INVISIBLE);
                    mBgImg.setVisibility(View.VISIBLE);
                    mBgImg.startAnimation(an);
                }
                animateFirst = !animateFirst;
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });
        mImgBg.startAnimation(anim);
        //根据时间设置 上午好 中午好 晚上好
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour >= 12 && hour < 18) {
            mDayTime.setText(R.string.noon);

        } else if (hour >= 0 && hour < 12) {
            mDayTime.setText(R.string.morning);
        } else {
            mDayTime.setText(R.string.evening);
        }
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IMimeCamBusiness business = ErmuBusiness.getMimeCamBusiness();
        ICamSettingBusiness camSettingBusiness = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnMimeCamChangedListener.class, this);
        business.unRegisterListener(OnMimeCamChangedErrorListener.class, this);
        business.unRegisterListener(OnDropGrantDeviceListener.class, this);
        camSettingBusiness.unRegisterListener(OnCheckUpgradeVersionListener.class, this);
        camSettingBusiness.unRegisterListener(OnGetCamUpdateStatusListener.class, this);
        ErmuBusiness.getPubCamCommentBusiness().unRegisterListener(OnStoreSuccessListener.class, this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.isHidden = hidden;
        if (!hidden) {
            refreshAdapter();
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                //List<String> deviceIds = adapter.getItemIds(firstVisibleItem, visibleItemCount, totalItemCount);
                //ErmuBusiness.getStreamMediaBusiness().preOpenLiveMedia(deviceIds);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        if (adapter == null) return;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            //List<String> deviceIds = adapter.getItemIds(firstVisibleItem, visibleItemCount, totalItemCount);
            //ErmuBusiness.getStreamMediaBusiness().preOpenLiveMedia(deviceIds);
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && visibleItemCount + firstVisibleItem == totalItemCount && !isLoading && (business.getNextPageNum() != -1 || isStoreChanged)) {
            Logger.d("NextPageNum: " + business.getNextPageNum());
            view.scrollTo(0, totalItemCount - 1);
            viewLoadBottom.setVisibility(View.VISIBLE);
            business.syncOldCamList();
            isLoading = true;
        }
    }

    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private boolean isLoading;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    @OnClick(value = {R.id.actionbar_adddev, R.id.add_cam, R.id.public_channel, R.id.error_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_adddev:
            case R.id.add_cam:
                String language = LanguageUtil.getLanguage();
                String baiduUid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
                if (language.equals("zh")) {
                    if (!TextUtils.isEmpty(baiduUid)) {
                        Fragment fragment1 = ConnAndBuyFragment.actionInstance(getActivity());
                        super.addToBackStack(fragment1);
                    } else {
                        WebActivity.actionStartWeb(getActivity(), WebActivity.BIND_BAI_DU_URL);
                    }
                } else {
                    Fragment fragment1 = ConnAndBuyFragment.actionInstance(getActivity());
                    super.addToBackStack(fragment1);
                }
                break;
            case R.id.public_channel:
                if (activity != null) activity.switchPubFragment();
                break;
            case R.id.error_btn:
                business.syncNewCamList();
                updateFlag = false;
                isLoading = true;
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        business.syncNewCamList();
        updateFlag = false;
        isLoading = true;
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Util.isNetworkConn(getActivity())) {
            List<MimeCamItem> itemList2 = business.getCamItemList();
            if (itemList2.size() > 0) {
                mNetError.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged(itemList2);
            } else {
                if (MemptyView.getVisibility() == View.VISIBLE) {
                    mNetError.setVisibility(View.INVISIBLE);
                } else {
                    mNetError.setVisibility(View.VISIBLE);
                }
            }
        } else {
            List<MimeCamItem> itemList1 = business.getCamItemList();
            if (itemList1.size() > 0) {
                loadView.setVisibility(View.INVISIBLE);
                mNetError.setVisibility(View.INVISIBLE);
            } else {
                if (MemptyView.getVisibility() == View.VISIBLE || loadView.getVisibility() == View.VISIBLE) {
                    mNetError.setVisibility(View.INVISIBLE);
                } else {
                    mNetError.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    private void refreshAdapter() {
        isLoading = false;
        if (!Util.isNetworkConn(getActivity())) {
            List<MimeCamItem> itemList1 = business.getCamItemList();
            if (itemList1.size() > 0) {
                adapter.notifyDataSetChanged(itemList1);
            } else {
                List<MimeCamItem> itemList3 = business.getCamItemList();
                if (itemList3.size() > 0) {
                    MemptyView.setVisibility(View.INVISIBLE);
                }
                if (MemptyView.getVisibility() == View.VISIBLE) {
                    mNetError.setVisibility(View.INVISIBLE);
                } else {
                    mNetError.setVisibility(View.VISIBLE);
                }
            }
        } else {
            List<MimeCamItem> itemList = business.getCamItemList();
            adapter.notifyDataSetChanged(itemList);

            if (isHidden) return;
            boolean visible = (loadView.getVisibility() == View.VISIBLE || adapter.getCount() > 0);
            if (visible) {
                showActionBar();
            } else {
                hideActionBar();
            }
            MemptyView.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
            if (MemptyView.getVisibility() == View.VISIBLE) {
                mNetError.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onMimeCamChanged() {
        isStoreChanged = false;
        loadView.setVisibility(View.GONE);
        viewLoadBottom.setVisibility(View.GONE);
        if (Util.isNetworkConn(getActivity())) {
            mPullableLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            viewError.setVisibility(View.GONE);
        } else {
            mPullableLayout.refreshFinish(PullToRefreshLayout.FAIL);
            textViewError.setText(getString(R.string.connect_server_fail));
            viewError.setVisibility(View.VISIBLE);
        }

        mNetError.setVisibility(View.INVISIBLE);
        refreshAdapter();
    }

    @Override
    public void onMimeCamChangedError(Business business) {
        isStoreChanged = false;
        viewLoadBottom.setVisibility(View.GONE);
        if (Util.isNetworkConn(getActivity())) {
            mPullableLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            textViewError.setText(getString(R.string.connect_server_fail)+"[" + business.getCode() + "]");
            viewError.setVisibility(View.VISIBLE);
        } else {
            mPullableLayout.refreshFinish(PullToRefreshLayout.FAIL);
            textViewError.setText(getString(R.string.connect_server_fail));
            viewError.setVisibility(View.VISIBLE);
        }
        refreshAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.i("onItemClick " + position);

        MimeCamItem info = (MimeCamItem) adapter.getItem(position);
        if (info.getItemType() == MimeCamItem.TYPE_MIME
                || info.getItemType() == MimeCamItem.TYPE_AUTHORIZE) {
            CamLive camLive = info.getItem();
            String deviceId = camLive.getDeviceId();

            if(camLive.getNeedupdate()==1&&camLive.getForceUpgrade()==1&&!camLive.isOffline()&&camLive.isPowerOn()){
                if(upgradeCountMap.get(deviceId)==null) {
                    upgradePosition = position;
                    upgDialogFlad = true;
                    camSettingBusiness.checkUpgradeVersion(deviceId);
            }
            }else{
                Fragment fragment = MineLiveFragment.actionInstance(deviceId);
                addToBackStack(getActivity(), fragment, true);
            }


//            if(camLive.getConnectType() == ConnectType.BAIDU) {
//                //IStreamMediaBusiness business = ErmuBusiness.getStreamMediaBusiness();
//                //business.openLiveMedia(deviceId);
//                Fragment fragment = BaiduMineLiveFragment.actionInstance(deviceId);
//                addToBackStack(fragment);
//            } else
            //if(camLive.getConnectType() == ConnectType.LINYANG) {
            //    Fragment fragment = LyyLiveFragment.actionInstance(deviceId);
            //    addToBackStack(getActivity(), fragment, true);
            //}else {
//            Fragment fragment = MineLiveFragment.actionInstance(deviceId);
//            addToBackStack(getActivity(), fragment, true);
//            }
        } else {
//            Fragment fragment = PublicLiveFragment.actionInstance(deviceId);
//            addToBackStack(fragment);
        }
    }

    @Override
    public void onDropGrantDevice(Business business) {
        if (dialog != null) dialog.dismiss();
        int code = business.getCode();
        if (code == BusinessCode.SUCCESS) {
            ErmuApplication.toast(getResources().getString(R.string.delete_succeed));
        } else if (code == BusinessCode.DEVICE_NOT_EXIST) {
            ErmuApplication.toast(getResources().getString(R.string.dev_is_not));
        } else if (code == BusinessCode.DELETE_DEVICE_GRANT_FAILE) {
            ErmuApplication.toast(getResources().getString(R.string.auth_err));
        } else if (code == BusinessCode.HTTP_ERROR) {
            ErmuApplication.toast(getResources().getString(R.string.network_error));
        } else {
            ErmuApplication.toast(getResources().getString(R.string.delete_error));
        }
    }


    @Override
    public void dropAuth(final String deviceId, String camName) {
        final CommonDialog commonDialog = new CommonDialog(getActivity());
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setTitle(getResources().getString(R.string.delete_cam))
                .setContent(getResources().getString(R.string.delete_sure) + " " + camName + " ")
                .setCancelText(getResources().getString(R.string.cancle_txt))
                .setOkText(getResources().getString(R.string.sure))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ErmuBusiness.getShareBusiness().dropGrantDevice(deviceId);
                        dialog = new SweetAlertDialog(getActivity());
                        dialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        dialog.setContentText(getResources().getString(R.string.deleteing_dev));
                        dialog.show();

                        commonDialog.dismiss();
                    }
                })
                .setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialog.dismiss();
                    }
                }).show();
    }

    private boolean isStoreChanged;

    @Override
    public void onStoreSuccess(Business business) {
        isStoreChanged = true;
    }

    @Override
    public void OnCheckUpgradeVersion(UpgradeVersion upgradeVersion, Business business) {
        if(business.isSuccess()&&upgradeVersion!=null){
            String desc = upgradeVersion.getDesc();
            String[] descs = desc.split("\n");
            showUpgradeTipDialog(upgradeVersion.getVersion(),descs);

        }
    }

    private void showUpgradeTipDialog(String version, String[] tip) {
        if(upgDialogFlad&&(upgDialog==null||!upgDialog.isShowing())){
            upgDialogFlad = false;
            upgDialog = new UpgradeTipDialog(getActivity(), version, tip);
            upgDialog.setClicklistener(new UpgradeTipDialog.ClickListenerInterface() {
                @Override
                public void doConfirm() {
                    upgDialogFlad = false;
                    upgDialog.dismiss();
                    MimeCamItem info = (MimeCamItem) adapter.getItem(upgradePosition);
                    if (info.getItemType() == MimeCamItem.TYPE_MIME){
                        CamLive camLive = info.getItem();
                        String deviceId = camLive.getDeviceId();
                        camSettingBusiness.checkCamFirmware(deviceId);
                        View upgradeView = adapter.getUpgradeView(deviceId);
                        final UpgradeViewloading loadingView = (UpgradeViewloading) upgradeView.findViewById(R.id.upgrade_view_loading);
                        loadingView.setVisibility(View.VISIBLE);
                        loadingView.startAnimation();
                    }else if(info.getItemType() == MimeCamItem.TYPE_AUTHORIZE){
                        Toast.makeText(getActivity(), R.string.need_upgrade_tip,Toast.LENGTH_SHORT).show();
                    }
                    upgDialog = null;

//                ErmuBusiness.getShareBusiness().grantShare(code);
                }

                @Override
                public void doCancel() {
                    upgDialogFlad = false;
                    upgDialog.dismiss();
                    upgDialog = null;
                }
            });
            upgDialog.show();
        }

    }

    @Override
    public void OnGetCamUpdateStatus(CamUpdateStatus status, Business bus) {
        if(getActivity()==null) return;

        upgradeStatusMap.put(status.getDeviceid(),status.getIntStatus());

        View upgradeView = adapter.getUpgradeView(status.getDeviceid());
        final UpgradeViewloading loadingView = (UpgradeViewloading) upgradeView.findViewById(R.id.upgrade_view_loading);
        Log.i("Upgrade",status.getIntStatus()+"VISIBLE");
        loadingView.setVisibility(View.VISIBLE);
        loadingView.startAnimation();

//        if (upgradeCountMap.get(status.getDeviceid())==null){
//
//            final Handler handler = new Handler(){
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    loadingView.setTextProgress(msg.what);
//                }
//            };
//            mTimer = new Timer();
//            mTimerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    count++;
//                    if (count >= 99) {
//                        mTimer.cancel();
//                    }
//                    Message message = new Message();
//                    message.what = count;
//                    handler.sendMessage(message);
//                }
//            };
//            //开始一个定时任务
//            mTimer.schedule(mTimerTask, 0, 200);
//        }


        Integer cou = (Integer)upgradeCountMap.get(status.getDeviceid());
        if(cou==null) cou = 0;
        loadingView.setTextProgress(cou);
        if(cou<99)cou++;
        upgradeCountMap.put(status.getDeviceid(),cou);
        switch (status.getIntStatus()) {
            case 1:
                loadingView.setTextTip(getString(R.string.firmware_update_now));
                break;
            case 2:
                loadingView.setTextTip(getString(R.string.firmware_start_download));
                break;
            case 3:
                loadingView.setTextTip(getString(R.string.firmware_download_success));
                break;
            case 4:
                loadingView.setTextTip(getString(R.string.firmware_start_upgrade));
                break;
            case 5:
                loadingView.setTextTip(getString(R.string.firmware_update_success_refresh));
                loadingView.setLoadingImg(5);
                upgradeCountMap.remove(status.getDeviceid());
                upgradeStatusMap.remove(status.getDeviceid());
                business.syncNewCamList();
                break;
            case -1:
                loadingView.setTextTip(getString(R.string.firmware_down_fail_refresh));
                loadingView.setLoadingImg(-1);
                upgradeCountMap.remove(status.getDeviceid());
                upgradeStatusMap.remove(status.getDeviceid());
                business.syncNewCamList();
                break;
            case -2:
                loadingView.setTextTip(getString(R.string.firmware_update_fail_refresh));
                loadingView.setLoadingImg(-2);
                upgradeCountMap.remove(status.getDeviceid());
                upgradeStatusMap.remove(status.getDeviceid());
                business.syncNewCamList();
                break;
        }
    }
}
