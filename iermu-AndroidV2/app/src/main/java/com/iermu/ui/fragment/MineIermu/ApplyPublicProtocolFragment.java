package com.iermu.ui.fragment.MineIermu;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * Created by xjy on 15/7/6.
 */
public class ApplyPublicProtocolFragment extends BaseFragment {

    @ViewInject(R.id.public_live)       TextView mPublicLive;
    @ViewInject(R.id.public_live_txt)   TextView mPublicLiveTxt;
    @ViewInject(R.id.actionbar_title)   TextView mTitle;
    @ViewInject(R.id.actionbar_back)    ImageView mBack;
    public static final String SHARE_DEVICEID = "deviceid";
    private String description;
    private String deviceId;
    private int connType;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
//        setCommonActionBar("直播条款和协议");
        setCustomActionBar(R.layout.actionbar_about);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this,view);
        mTitle.setText(getString(R.string.terms_protocol));
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId) {
        ApplyPublicProtocolFragment fragment = new ApplyPublicProtocolFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SHARE_DEVICEID, deviceId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apply_public_protocol, container, false);
        ViewHelper.inject(this, view);

        setSpanPublicLive();
        Bundle bundle = getArguments();
        deviceId = bundle.getString(SHARE_DEVICEID);
        mPublicLive.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        return view;
    }

    private void setSpanPublicLive() {
        String str = getResources().getString(R.string.read_agree);
        String protocol = getResources().getString(R.string.public_protocol);
        int strLength = str.length();
        int proLength = protocol.length();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        TextViewURLSpan myURLSpan = new TextViewURLSpan();
        style.setSpan(myURLSpan,strLength - proLength,strLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPublicLiveTxt.setText(style);
        mPublicLiveTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick(value = {R.id.buttonBegin, R.id.public_live,R.id.actionbar_back})
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.buttonBegin:
            Fragment frangment = ApplyPublicEditFragment.actionInstance(getActivity(), deviceId);
            addToBackStack(getActivity(), frangment);
            break;
        case R.id.public_live://TODO 公开直播协议
//            WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_AGREEMENT);
            break;
        case R.id.actionbar_back:
            popBackStack();
            break;
        }
    }

    private class TextViewURLSpan extends ClickableSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.mycam_talk));
            ds.setUnderlineText(false); //去掉下划线
        }
        @Override
        public void onClick(View widget) {//点击事件
            WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_AGREEMENT);
        }
    }

}
