package com.iermu.ui.fragment.person;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.listener.OnUpdateUserNameListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommitDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xjy on 15/11/27.
 */
public class ChangeUserNameFragment extends BaseFragment implements TextWatcher, OnUpdateUserNameListener, View.OnFocusChangeListener {

    @ViewInject(R.id.actionbar_finish)  TextView actionbarFinish;
    @ViewInject(R.id.actionbar_title)   TextView actionTitle;
    @ViewInject(R.id.edit_user_name)    EditText editName;
    @ViewInject(R.id.title_bg)          LinearLayout titleBg;
    @ViewInject(R.id.name_close)        RelativeLayout mNameClose;

    private static final  String NICK_NAME = "nickname";
    private String mNickName;
    private CommitDialog dialog;
    private String symbol = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……& ;*（）——+|{}【】‘；：”“’》《---。，、？|-]";
    private String tmp = "";
    private int count = 0;
    OnUserNameChanged listener;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_about);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        actionbarFinish.setText(R.string.text_button);
        actionTitle.setText(R.string.change_user_name);
        titleBg.setBackgroundResource(R.color.devices_help_btn_bg);
    }

    public static Fragment actionInstance(FragmentActivity activity) {
        return new ChangeUserNameFragment();
    }
    public static Fragment actionInstance(FragmentActivity activity ,String mNickName){
        Fragment fragment = new ChangeUserNameFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NICK_NAME, mNickName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_username, container, false);
        ViewHelper.inject(this, view);

        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.registerListener(OnUpdateUserNameListener.class, this);
        initView();
        return view;
    }

    private void initView() {
        InputUtil.showSoftInput(getActivity(), editName);
        mNickName = getArguments().getString(NICK_NAME);
        editName.setText(mNickName);
        String uName = editName.getText().toString().trim();
        editName.setSelection(uName.length());
        if (TextUtils.isEmpty(uName)) {
            actionbarFinish.setEnabled(false);
            actionbarFinish.setFocusable(false);
            actionbarFinish.setTextColor(getResources().getColor(R.color.text_button));
        }
        editName.addTextChangedListener(this);
        editName.setOnFocusChangeListener(this);
    }

    private void showDialog() {
        dialog = new CommitDialog(getActivity());
        dialog.show();
        dialog.setStatusText(getResources().getString(R.string.dialog_commit_text));
    }

    public void setOnChangeListener(OnUserNameChanged listener) {
        this.listener = listener;
    }

    @OnClick(value = {R.id.actionbar_finish, R.id.actionbar_back, R.id.name_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_finish:
                InputUtil.hideSoftInput(getActivity(), editName);
                String uName = editName.getText().toString();
                if (Util.isNetworkConn(getActivity())) {
                    if (!TextUtils.isEmpty(uName)) {
                        charLength(uName);
                        if (count > 0 && count < 4 || count > 20) {
                            customToast(getString(R.string.nick_name_length));
                            return;
                        }
                    }
                    String nameBegin = uName.substring(0,1);
                    if (isBegin(nameBegin) || symbol.contains(nameBegin)){
                        customToast(getString(R.string.username_regular));
                    } else {
                        showDialog();
                        ErmuBusiness.getAccountAuthBusiness().updateUserName(uName);
                    }
                } else {
                    ErmuApplication.toast(getResources().getString(R.string.no_network));
                }
                break;
            case R.id.actionbar_back:
                popBackStack();
                InputUtil.hideSoftInput(getActivity(), editName);
                break;
            case R.id.name_close:
                editName.setText("");
                mNameClose.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        InputUtil.hideSoftInput(getActivity(), editName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.unRegisterListener(OnUpdateUserNameListener.class, this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            mNameClose.setVisibility(View.VISIBLE);
        } else {
            mNameClose.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
//        String userName = editName.getText().toString().trim();
//        symbolMatch(userName);
//        charLength(userName);
        if(s.length() < 2) {
            actionbarFinish.setEnabled(false);
            actionbarFinish.setFocusable(false);
            actionbarFinish.setTextColor(getResources().getColor(R.color.text_button));
        } else {
            actionbarFinish.setEnabled(true);
            actionbarFinish.setFocusable(true);
            actionbarFinish.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void symbolMatch(String nickName) {
        if (nickName.equals(tmp)) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nickName.length(); i++) {
            if (symbol.indexOf(nickName.charAt(i)) < 0) {
                sb.append(nickName.charAt(i));
            }
        }
        tmp = sb.toString();
        editName.setText(tmp);
    }

    private void charLength(String str) {
        count = 0;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        for(int i = 0;i < str.length();i++) {
            char item = str.charAt(i);
            Matcher m = p.matcher(item+"");
            if (m.find()) {
                count = count + 2;
            } else {
                count = count + 1;
            }
            if (count > 20) {
//                editName.setText(str.substring(0, i));
//                customToast(getString(R.string.nick_name_length));
                break;
            }
        }
//        if (count < 4 || count > 20) {
//            actionbarFinish.setEnabled(false);
//            actionbarFinish.setFocusable(false);
//            actionbarFinish.setTextColor(getResources().getColor(R.color.text_button));
//        } else {
//            actionbarFinish.setEnabled(true);
//            actionbarFinish.setFocusable(true);
//            actionbarFinish.setTextColor(getResources().getColor(R.color.white));
//        }
    }

    public static boolean isBegin(String nickName){
        String regex=".*[\\d-].*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickName);
        return matcher.find();
    }

    @Override
    public void onUpdateUserName(final Business business) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) dialog.dismiss();
                int code = business.getCode();
                switch (code) {
                    case BusinessCode.SUCCESS:
                        String uName = editName.getText().toString();
                        if (listener != null) listener.changeUserName(uName);
                        popBackStack();
                        break;
                    case BusinessCode.USER_CHECK_USERNAME_FAILED:
                        ErmuApplication.toast(getString(R.string.nickname_failed));
                        break;
                    case BusinessCode.USER_USERNAME_BADWORD:
                        ErmuApplication.toast(getString(R.string.nickname_disable));
                        break;
                    case BusinessCode.USER_USERNAME_EXISTS:
                        ErmuApplication.toast(getString(R.string.user_name_exist));
                        break;
                    case BusinessCode.UPDATE_USERNAME_FAILED:
                        ErmuApplication.toast(getString(R.string.update_name_failed));
                        break;
                    default:
                        ErmuApplication.toast(getString(R.string.update_name_failed));
                        break;
                }
            }
        }, 500);
    }
    private void customToast(String str) {
        Toast toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String name = editName.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            mNameClose.setVisibility(View.VISIBLE);
        } else {
            mNameClose.setVisibility(View.GONE);
        }
    }

    interface OnUserNameChanged {
        void changeUserName(String uName);
    }
}
