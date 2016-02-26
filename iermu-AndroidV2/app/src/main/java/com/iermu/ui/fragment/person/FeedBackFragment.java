package com.iermu.ui.fragment.person;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.listener.OnFeedBackListener;
import com.iermu.client.model.Business;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommitDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * Created by xjy on 15/11/27.
 */
public class FeedBackFragment extends BaseFragment implements TextWatcher, OnFeedBackListener {

    @ViewInject(R.id.feed)              EditText mFeed;
    @ViewInject(R.id.textViewNum)       TextView mTextViewNum;
    @ViewInject(R.id.connect_way)       EditText connectMay;

    private CommitDialog dialog;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.person_feedback)
            .setCommonFinish(R.string.commit);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    public static Fragment acitonInstance(FragmentActivity activity) {
        return new FeedBackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgent_feedback, container, false);
        ViewHelper.inject(this, view);
        mFeed.addTextChangedListener(this);

        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.registerListener(OnFeedBackListener.class, this);
        return view;
    }

    @OnClick(value = {R.id.actionbar_finish, R.id.actionbar_back})
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.actionbar_finish:
            final String connectMethod = connectMay.getText().toString().trim();
            final String feedBack = mFeed.getText().toString().trim();
            if (Util.isNetworkConn(getActivity())) {
                if (TextUtils.isEmpty(feedBack)) {
                    customToast(getString(R.string.feedback_content_null));
                } else if (TextUtils.isEmpty(connectMethod)) {
                    customToast(getString(R.string.input_contact));
                } else {
                    showDialog();
                    InputUtil.hideSoftInput(getActivity(), connectMay);
                    ErmuBusiness.getAccountAuthBusiness().feedBack(feedBack, connectMethod);
                }
            } else {
                ErmuApplication.toast(getString(R.string.no_net));
            }
            break;
        case R.id.actionbar_back:
            popBackStack();
            InputUtil.hideSoftInput(getActivity(), connectMay);
            break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        InputUtil.hideSoftInput(getActivity(), connectMay);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.unRegisterListener(OnFeedBackListener.class, this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = s.length();
        if (length <= 140) {
            String feed = length + "/140";
            mTextViewNum.setText(feed);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void showDialog() {
        dialog = new CommitDialog(getActivity());
        dialog.show();
        dialog.setStatusText(getResources().getString(R.string.dialog_commit_text));
    }

    private void customToast(String str) {
        Toast toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onFeedBack(final Business business) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) dialog.dismiss();
                if (business.isSuccess()) {
                    popBackStack();
                } else {
                    ErmuApplication.toast(getString(R.string.feedback_failed));
                }
            }
        }, 500);
    }
}
