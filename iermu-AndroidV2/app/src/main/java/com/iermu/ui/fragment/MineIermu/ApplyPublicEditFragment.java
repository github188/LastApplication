package com.iermu.ui.fragment.MineIermu;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamShareBusiness;
import com.iermu.client.listener.OnCamShareChangedListener;
import com.iermu.client.listener.OnCancleShareListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ShareType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.camseting.EditCamNameFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.CommonDialog;
import com.iermu.ui.view.SwitchButtonNew;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;


/**
 * Created by xjy on 15/7/6.
 */
public class ApplyPublicEditFragment extends BaseFragment implements TextWatcher,
        OnCamShareChangedListener, EditCamNameFragment.OnDevNameChange, OnCancleShareListener {

    public static final String SHARE_DEVICEID = "deviceid";
    private String description;
    private String deviceId;
    private String mIntroTxt;
    private String thumbnail;

    @ViewInject(R.id.intro)
    EditText mIntro;
    @ViewInject(R.id.textViewNum)
    TextView textViewNum;
    @ViewInject(R.id.cam_img)
    ImageView mCamImg;
    @ViewInject(R.id.cam_name)
    TextView mCamName;
    @ViewInject(R.id.update_name)
    LinearLayout mUpdateName;
    @ViewInject(R.id.switch_button)
    SwitchButtonNew switchButton;
    @ViewInject(R.id.title_bg)
    LinearLayout titleBg;

    CommonCommitDialog commitDialog;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.live_info);
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId) {
        ApplyPublicEditFragment fragment = new ApplyPublicEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SHARE_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment actionInstance(FragmentActivity activity) {
        return new ApplyPublicEditFragment();
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apply_public_edit, container, false);
        ViewHelper.inject(this, view);
        Bundle bundle = getArguments();
        deviceId = bundle.getString(SHARE_DEVICEID);
        mIntro.addTextChangedListener(this);
        switchButton.setFocusable(false);
        switchButton.setEnabled(false);
        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.registerListener(OnCamShareChangedListener.class, this);
        business.registerListener(OnCancleShareListener.class, this);
        refreshView();
        Picasso.with(getActivity()).load(TextUtils.isEmpty(thumbnail) ? "default" : thumbnail)
                .priority(Picasso.Priority.HIGH)
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.iermu_thumb)
                .into(mCamImg);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamShareBusiness business = ErmuBusiness.getShareBusiness();
        business.unRegisterListener(OnCamShareChangedListener.class, this);
        business.unRegisterListener(OnCancleShareListener.class, this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        refreshView();
    }

    private void refreshView() {
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        description = live.getDescription();
        thumbnail = live.getThumbnail();
        mCamName.setText(description);
    }

    @OnClick(value = {R.id.buttonOk, R.id.update_name, R.id.actionbar_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
                int shareType = live.getShareType();
                if (shareType == ShareType.PRIVATE) {
                    mIntroTxt = mIntro.getText().toString().trim();
                    ErmuBusiness.getShareBusiness().createShare(deviceId, mIntroTxt, ShareType.PUB_NOTCLOUD);
                    commitDialog = new CommonCommitDialog(getActivity());
                    commitDialog.show();
                    commitDialog.setStatusText(getString(R.string.open_public_share));
                } else {
                    showDialog();
                }
                break;
            case R.id.update_name:
                Fragment fragment = EditCamNameFragment.actionInstance(deviceId);
                ((EditCamNameFragment) fragment).setOnControlListener(this);
                addToBackStack(getActivity(), fragment);
                break;
            case R.id.actionbar_back:
                popBackStack();
                InputUtil.hideSoftInput(getActivity(), mIntro);
        }
    }

    private void showDialog() {
        final CommonDialog commonDialog = new CommonDialog(getActivity());
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setTitle(getString(R.string.iermu_prompt));
        commonDialog.setContent(getString(R.string.share_public));
        commonDialog.setCancelText(getString(R.string.goon_privacy_live));
        commonDialog.setOkText(getString(R.string.need_public_live));
        commonDialog.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitDialog = new CommonCommitDialog(getActivity());
                commitDialog.show();
                commitDialog.setStatusText(getString(R.string.close_privacy_share));
                ErmuBusiness.getShareBusiness().cancleShare(deviceId);
                commonDialog.dismiss();
            }
        });
        commonDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
            }
        }).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = s.length();
        if (length <= 40) {
            String str = length + "/40";
            textViewNum.setText(str);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onShareCreated(String deviceId, Business bus) {
        if (commitDialog != null) commitDialog.dismiss();
        String shareLink = ErmuBusiness.getShareBusiness().getShareLink(deviceId);
        int code = bus.getCode();
        switch (code) {
            case BusinessCode.SUCCESS://网路请求成功，百度返回的uk和shareId为空
                Fragment fragment = ApplyPublicSuccessFragment.actionInstance(getActivity(), deviceId, shareLink);
                addJumpToBackStack(fragment, ShareControlFragment.class);
                break;
            case BusinessCode.FORBIDENT_CREATE_SHARE:
                ErmuApplication.toast(getString(R.string.dev_no_share));
                break;
            default:
                ErmuApplication.toast(getString(R.string.create_share_fail));
                break;
        }
    }

    @Override
    public void callBack(String name) {
        if (!TextUtils.isEmpty(name)) mCamName.setText(name);
    }

    @Override
    public void onCancleShare(Business bus) {
        if (bus.getCode() == BusinessCode.SUCCESS) {
            mIntroTxt = mIntro.getText().toString().trim();
            ErmuBusiness.getShareBusiness().createShare(deviceId, mIntroTxt, ShareType.PUB_NOTCLOUD);
            commitDialog.setStatusText(getString(R.string.open_public_share));
        } else {
            if (commitDialog != null) commitDialog.dismiss();
            ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
        }
    }
}
