package com.iermu.ui.fragment.MineIermu;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamShareBusiness;
import com.iermu.client.listener.OnCamShareChangedListener;
import com.iermu.client.listener.OnCancleShareListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.ShareType;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.CommonDialog;
import com.mob.tools.utils.UIHandler;
import com.squareup.picasso.Picasso;
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
 * Created by zhoushaopei on 15/8/15.
 */
public class ShareLinkFragment extends BaseFragment implements PlatformActionListener, Handler.Callback
        , OnCamShareChangedListener, OnCancleShareListener {

    public static final String SHARE_DEVICEID = "deviceid";
    private static final int MSG_TOAST = 1;
    private static final int MSG_ACTION_CCALLBACK = 2;
    private static final int MSG_CANCEL_NOTIFY = 3;

    @ViewInject(R.id.is_share)      TextView mIsShare;
    @ViewInject(R.id.share_wx)      TextView mShareWx;
    @ViewInject(R.id.share_peng)    TextView mSharePeng;
    @ViewInject(R.id.share_link)    TextView mShareLink;
    @ViewInject(R.id.cam_img)       ImageView mCamImg;
    @ViewInject(R.id.cam_name)      TextView mDescription;
    @ViewInject(R.id.share_content) TextView mShareContent;

    private String deviceId;
    private int shareType;
    private CommonCommitDialog commitDialog;
    private int privateShareType = -1;
    private int SHARE_WX = 0;
    private int SHARE_MOMENT = 1;
    private int SHARE_LINK = 2;
    private int SHARE_CLOSE = -1;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_share_secret);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId) {
        ShareLinkFragment fragment = new ShareLinkFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SHARE_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_link_layout, container, false);
        ViewHelper.inject(this, view);
        ShareSDK.initSDK(getActivity());
        Bundle bundle = getArguments();
        deviceId = bundle.getString(SHARE_DEVICEID);

        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.registerListener(OnCamShareChangedListener.class, this);
        business.registerListener(OnCancleShareListener.class, this);
        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.unRegisterListener(OnCamShareChangedListener.class, this);
        business.unRegisterListener(OnCancleShareListener.class, this);
    }

    private void refreshView() {
        String liveShare = getString(R.string.mime_live_share);
        String livePlay = getString(R.string.live_play);
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        mDescription.setText(String.format(livePlay, live.getDescription()));
        mShareContent.setText(String.format(liveShare, live.getDescription()));
        shareType = (live != null) ? live.getShareType() : 0;
        String thumbnail = (live != null) ? live.getThumbnail() : "default";
        thumbnail = TextUtils.isEmpty(thumbnail) ? "default" : thumbnail;

        Picasso.with(getActivity()).load(thumbnail)
                .placeholder(R.drawable.iermu_thumb)
                .priority(Picasso.Priority.HIGH)
                .config(Bitmap.Config.ARGB_8888)
                .into(mCamImg);

        mIsShare.setVisibility((shareType == ShareType.PRI_NOTCLOUD) ? View.VISIBLE : View.INVISIBLE);
    }

    @OnClick(value = {R.id.close, R.id.share_wx, R.id.share_peng, R.id.share_link, R.id.is_share, R.id.timer, R.id.password})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                popBackStack();
                break;
            case R.id.share_wx:
                onShareClick(SHARE_WX, mShareWx);
                break;
            case R.id.share_peng:
                onShareClick(SHARE_MOMENT, mSharePeng);
                break;
            case R.id.share_link:
                onShareClick(SHARE_LINK, mShareLink);
                break;
            case R.id.is_share:
                if (!Util.isNetworkConn(getActivity())) {
                    ErmuApplication.toast(getString(R.string.no_net));
                    break;
                }
                privateShareType = SHARE_CLOSE;
                if (commitDialog == null) {
                    commitDialog = new CommonCommitDialog(getActivity());
                }
                commitDialog.show();
                commitDialog.setStatusText(getString(R.string.close_privacy_share));
                ErmuBusiness.getShareBusiness().cancleShare(deviceId);
                break;
            case R.id.timer:
                toast();
                break;
            case R.id.password:
                toast();
                break;
        }
    }

    private void toast() {
        Toast toast = Toast.makeText(getActivity(), getString(R.string.develop_now), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 点击三个分享按钮时的操作
     *
     * @param type       分享类型
     * @param actionView 被点击的view
     */
    private void onShareClick(int type, View actionView) {
        if (!Util.isNetworkConn(getActivity())) {
            ErmuApplication.toast(getString(R.string.no_net));
            return;
        }

        privateShareType = type;
        actionView.setEnabled(false);
        mIsShare.setEnabled(false);
        if (shareType == ShareType.PRIVATE) {
            commitDialog = new CommonCommitDialog(getActivity());
            commitDialog.show();
            commitDialog.setStatusText(getString(R.string.open_privacy_share));
            ErmuBusiness.getShareBusiness().createShare(deviceId, "", ShareType.PRI_NOTCLOUD);
        } else if (shareType == ShareType.PRI_NOTCLOUD) {
            share(type);
            setDelayEnable(actionView);
            setDelayEnable(mIsShare);
        } else if (shareType == ShareType.PUB_NOTCLOUD) {
            showSwitchDialog();
        }
    }

    private void share(int type) {
        if (type == SHARE_WX) {
            Platform.ShareParams weChat = shareParams();
            Platform weiXin = ShareSDK.getPlatform(getActivity(), Wechat.NAME);
            if (weiXin.isClientValid()) {
                weiXin.setPlatformActionListener(this);
                weiXin.share(weChat);
            } else {
                ErmuApplication.toast(getString(R.string.no_wechat_client));
            }
        } else if (type == SHARE_MOMENT) {
            Platform.ShareParams weChatMoments = shareParams();
            Platform wXP = ShareSDK.getPlatform(getActivity(), WechatMoments.NAME);
            if (wXP.isClientValid()) {
                wXP.setPlatformActionListener(this);
                wXP.share(weChatMoments);
            } else {
                ErmuApplication.toast(getString(R.string.no_wechat_client));
            }
        } else {
            String shareLink = ErmuBusiness.getShareBusiness().getShareLink(deviceId);
            ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(shareLink);
            ErmuApplication.toast(getString(R.string.copy_link));
            Logger.i("---" + shareLink);
        }
    }

    /**
     * 设置view延迟取消禁用
     *
     * @param view
     */
    private void setDelayEnable(final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 2000);
    }

    @Override
    public void onShareCreated(String deviceId, Business bus) {
        if (commitDialog != null) commitDialog.cancel();
        if (bus.isSuccess()) {
            share(privateShareType);
            setDelayEnable(mShareWx);
            setDelayEnable(mSharePeng);
            setDelayEnable(mShareLink);
            setDelayEnable(mIsShare);
        } else {
            ErmuApplication.toast(getString(R.string.open_share_fail));
        }
        refreshView();
    }

    @Override
    public void onCancleShare(Business bus) {
        if (bus.isSuccess()) {
            if (privateShareType != SHARE_CLOSE) {
                commitDialog.setStatusText(getString(R.string.open_privacy_share));
                ErmuBusiness.getShareBusiness().createShare(deviceId, "", ShareType.PRI_NOTCLOUD);
            } else {
                if (commitDialog != null) {
                    commitDialog.cancel();
                }
            }
        } else {
            if (commitDialog != null) {
                commitDialog.cancel();
            }
            ErmuApplication.toast(getString(R.string.close_share_fail));
        }
        refreshView();
    }

    /**
     * 提示正在公开分享
     */
    private void showSwitchDialog() {
        final CommonDialog commonDialog = new CommonDialog(getActivity());
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setTitle(getString(R.string.iermu_prompt));
        commonDialog.setContent(getString(R.string.share_privacy));
        commonDialog.setCancelText(getString(R.string.goon_public_live));
        commonDialog.setOkText(getString(R.string.need_share_privacy));
        commonDialog.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitDialog = new CommonCommitDialog(getActivity());
                commitDialog.show();
                commitDialog.setStatusText(getString(R.string.close_public_share));
                ErmuBusiness.getShareBusiness().cancleShare(deviceId);
                commonDialog.dismiss();
            }
        });
        commonDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
                mShareWx.setEnabled(true);
                mSharePeng.setEnabled(true);
                mShareLink.setEnabled(true);
            }
        }).show();
    }

    private Platform.ShareParams shareParams() {
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        shareType = (live != null) ? live.getShareType() : 0;
        String thumbnail = (live != null) ? live.getThumbnail() : "";
        String description = (live != null) ? live.getDescription() : "";
        String shareLink = ErmuBusiness.getShareBusiness().getShareLink(deviceId);

        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setTitle(mDescription.getText().toString().trim());
        wechat.setText(mShareContent.getText().toString().trim());
        wechat.setUrl(shareLink);
        wechat.setImageUrl(thumbnail);
        wechat.setShareType(Platform.SHARE_WEBPAGE);
        return wechat;
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

    public boolean handleMessage(Message msg) {
        if (getActivity() == null) return false;
        switch (msg.arg1) {
            case 1: // 成功
                ErmuApplication.toast(getActivity().getResources().getString(R.string.share_success));
                break;
            case 2: // 失败
                ErmuApplication.toast(getActivity().getResources().getString(R.string.share_fail));
                break;
            case 3: // 取消
                ErmuApplication.toast(getActivity().getResources().getString(R.string.cancel_share));
                break;
        }
        return false;
    }
}
