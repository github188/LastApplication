package com.iermu.ui.fragment.MineIermu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamShareBusiness;
import com.iermu.client.listener.OnCancleShareListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.ShareType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.CommonDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;


/**
 * Created by zhoushaopei on 15/8/15.
 */
public class ShareControlFragment extends BaseFragment implements OnCancleShareListener {

    public static final String SHARE_DEVICEID = "deviceid";
    public static final int NOT_SHARE = 0;  //未分享
    public static final int SHARE_PUBLIC = 1;  //公共直播
    public static final int SHARE_SECRET = 2;  //私密分享

    @ViewInject(R.id.share_link)
    LinearLayout mShareLink;
    @ViewInject(R.id.share_public)
    LinearLayout mSharePublic;
    @ViewInject(R.id.close_share)
    Button mCloseShare;
    @ViewInject(R.id.link_img)
    ImageView mLinkImg;
    @ViewInject(R.id.public_img)
    ImageView mPubImg;

    private String deviceId;
    private int shareType;
    private CommonCommitDialog commitDialog;


    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(getString(R.string.my_share));
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId) {
        ShareControlFragment fragment = new ShareControlFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SHARE_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_control_layout, container, false);
        ViewHelper.inject(this, view);

        Bundle bundle = getArguments();
        deviceId = bundle.getString(SHARE_DEVICEID);

        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.registerListener(OnCancleShareListener.class, this);
        refreshView();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refreshView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.unRegisterListener(OnCancleShareListener.class, this);
    }

    @OnClick(value = {R.id.share_link, R.id.share_public, R.id.close_share})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_link:
                Fragment fragmentPri = ShareLinkFragment.actionInstance(getActivity(), deviceId);
                addToBackStack(fragmentPri);
                break;
            case R.id.share_public:
                if (shareType == ShareType.PUB_NOTCLOUD) {
                    Fragment fragmentPub = ApplyPublicSuccessFragment.actionInstance(getActivity(), deviceId, "");
                    addToBackStack(fragmentPub);
                } else {
                    Fragment fragmentPub = ApplyPublicProtocolFragment.actionInstance(getActivity(), deviceId);
                    addToBackStack(fragmentPub);
                }
                break;
            case R.id.close_share:
                final CommonDialog commonDialog = new CommonDialog(getActivity());
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog.setTitle(getString(R.string.close_share))
                        .setContent(getString(R.string.no_longer_look_live))
                        .setCancelText(getString(R.string.cancle_txt))
                        .setOkText(getString(R.string.btn_cam_ok))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ErmuBusiness.getShareBusiness().cancleShare(deviceId);
                                commonDialog.dismiss();
                                commitDialog = new CommonCommitDialog(getActivity());
                                commitDialog.show();
                                commitDialog.setStatusText(getString(R.string.close_share_now));
                            }
                        })
                        .setCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonDialog.dismiss();
                            }
                        }).show();
                break;
        }
    }

    @Override
    public void onCancleShare(Business bus) {
        if (commitDialog != null) commitDialog.dismiss();
        if (!bus.isSuccess()) {
            ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
        }
        refreshView();
    }

    private void refreshView() {
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        shareType = live.getShareType();
        if (shareType == NOT_SHARE) {
            mCloseShare.setVisibility(View.GONE);
            mLinkImg.setVisibility(View.GONE);
            mPubImg.setVisibility(View.GONE);
        } else if (shareType == SHARE_PUBLIC) {
            mPubImg.setVisibility(View.VISIBLE);
            mLinkImg.setVisibility(View.GONE);
            mCloseShare.setVisibility(View.VISIBLE);
        } else if (shareType == SHARE_SECRET) {
            mLinkImg.setVisibility(View.VISIBLE);
            mPubImg.setVisibility(View.GONE);
            mCloseShare.setVisibility(View.VISIBLE);
        }
    }
}
