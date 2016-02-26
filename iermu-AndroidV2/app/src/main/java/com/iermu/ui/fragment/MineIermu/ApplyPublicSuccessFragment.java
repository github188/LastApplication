package com.iermu.ui.fragment.MineIermu;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamShareBusiness;
import com.iermu.client.listener.OnCancleShareListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.publicchannel.PublicLiveFragment;
import com.iermu.ui.view.CommonCommitDialog;
import com.mob.tools.utils.UIHandler;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by xjy on 15/7/6.
 */
public class ApplyPublicSuccessFragment extends BaseFragment implements PlatformActionListener, Handler.Callback, OnCancleShareListener {

    private static final int MSG_TOAST = 1;
    private static final int MSG_ACTION_CCALLBACK = 2;
    private static final int MSG_CANCEL_NOTIFY = 3;
    public static final String SHARE_DEVICEID = "deviceid";
    private String deviceId;
    private CommonCommitDialog dialog;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(getString(R.string.my_live_cam));
        setCommonFinishHided();

    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId, String link) {
        ApplyPublicSuccessFragment fragment = new ApplyPublicSuccessFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SHARE_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apply_public_success, container, false);
        ViewHelper.inject(this, view);
        Bundle bundle = getArguments();
        deviceId = bundle.getString(SHARE_DEVICEID);
        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.registerListener(OnCancleShareListener.class, this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.unRegisterListener(OnCancleShareListener.class, this);
    }

    @OnClick(value = {R.id.share_wx, R.id.share_qq, R.id.share_peng, R.id.share_link, R.id.cancle_share, R.id.cam_live,R.id.actionbar_back})
    private void onClick(View view) {
        switch (view.getId()) {
        case R.id.share_wx:
            Platform.ShareParams wechat = shareParams();
            Platform weixin = ShareSDK.getPlatform(getActivity(), Wechat.NAME);
            weixin.setPlatformActionListener(this);
            weixin.share(wechat);
            break;
        case R.id.share_qq:
            //下版加上
            break;
        case R.id.share_peng:
            Platform.ShareParams wechatMoments = shareParams();
            Platform wXP = ShareSDK.getPlatform(getActivity(), WechatMoments.NAME);
            wXP.setPlatformActionListener(this);
            wXP.share(wechatMoments);
            break;
        case R.id.share_link:
            String shareLink = ErmuBusiness.getShareBusiness().getShareLink(deviceId);
            ClipboardManager cmb = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(shareLink);
            ErmuApplication.toast(getString(R.string.copy_link));
            break;
        case R.id.cancle_share:
            dialog = new CommonCommitDialog(getActivity());
            dialog.show();
            dialog.setStatusText(getString(R.string.close_public_share));
            ErmuBusiness.getShareBusiness().cancleShare(deviceId);
            break;
        case R.id.cam_live:
            CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
            String deviceId = live.getDeviceId();
            String shareId = live.getShareId();
            String uk = live.getUk();
            Fragment fragment = PublicLiveFragment.actionInstance(deviceId, shareId, uk, false);
            addJumpToPopAllStack(fragment);
            break;
        }
    }

    private Platform.ShareParams shareParams () {
        String livePlay = getString(R.string.live_play);
        String liveShare = getString(R.string.mime_live_share);
        String shareLink = ErmuBusiness.getShareBusiness().getShareLink(deviceId);
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        String description = (live!=null) ? live.getDescription() : "";
        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setTitle(String.format(livePlay, description));
        wechat.setText(String.format(liveShare, description));
        wechat.setUrl(shareLink);
        wechat.setShareType(Platform.SHARE_WEBPAGE);
        return wechat;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.arg1) {
            case 1: { // 成功
                ErmuApplication.toast(getString(R.string.share_success));
            }
            break;
            case 2: { // 失败
                ErmuApplication.toast(getString(R.string.share_fail));
            }
            break;
            case 3: { // 取消
                ErmuApplication.toast(getString(R.string.cancel_share));
            }
            break;
        }
        return false;
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {
        t.printStackTrace();
        //错误监听,handle the error msg
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onCancel(Platform platform, int action) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onCancleShare(Business bus) {
        if (dialog != null) dialog.dismiss();
        if(bus.getCode()== BusinessCode.SUCCESS) {
            popBackStack();
        }
    }

}
