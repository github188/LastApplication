package com.iermu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.business.api.response.ErrorCode;
import com.iermu.client.listener.OnAccountAuthListener;
import com.iermu.client.model.Business;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommitDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xjy on 15/11/30.
 */
public class MailLogin extends BaseActivity implements View.OnClickListener, TextWatcher, OnAccountAuthListener {

    @ViewInject(R.id.login_btn)         Button mLoginBtn;
    @ViewInject(R.id.input_mail)        EditText mMailEdit;
    @ViewInject(R.id.hide_or_show_btn)  TextView hideShowBtn;
    @ViewInject(R.id.input_mail_password)EditText mPasswordEdit;
    @ViewInject(R.id.mail_close)       ImageView mMailClose;


    private String mail;
    private String password;
    private CommitDialog loginDialog;

    private static final String INTENT_SHAREAUTH_URL = "grant";
    private final static String regEx_email = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_login_mail);
        ViewHelper.inject(this);

        ErmuBusiness.getAccountAuthBusiness().registerListener(OnAccountAuthListener.class, this);
        mPasswordEdit.setTag(0);
        mMailEdit.addTextChangedListener(this);
        mPasswordEdit.addTextChangedListener(this);
    }

    @OnClick(value = {R.id.login_btn, R.id.mail_login_back, R.id.login_baidu_mail, R.id.hide_or_show_btn, R.id.mail_close, R.id.fast_register})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_btn:
                dialogShow();
                InputUtil.hideSoftInput(this, mMailEdit);
                InputUtil.hideSoftInput(this, mPasswordEdit);
                if (Util.isNetworkConn(this)) {
                    InputUtil.hideSoftInput(this, mMailEdit);
                    InputUtil.hideSoftInput(this, mPasswordEdit);
                    mail = mMailEdit.getText().toString().trim();
                    password = mPasswordEdit.getText().toString().trim();
                    IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
                    business.registerAccount(mail, password);
                } else {
                    ErmuApplication.toast(getResources().getString(R.string.check_network));
                }
                break;
            case R.id.mail_login_back:
                finish();
                break;
            case R.id.login_baidu_mail:
                intent = getIntent();
                if (intent.hasExtra(INTENT_SHAREAUTH_URL)) {
                    String extra = intent.getStringExtra(INTENT_SHAREAUTH_URL);
                    BaiDuLoginActivity.actionShareAuth(this, extra);
                } else {
                    BaiDuLoginActivity.actionStartBaiduLogin(this, false);
                }
                break;
            case R.id.hide_or_show_btn:
                boolean bShowPwd = Integer.parseInt(mPasswordEdit.getTag().toString()) == 1;
                mPasswordEdit.setInputType(bShowPwd ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mPasswordEdit.setTag(bShowPwd ? 0 : 1);
                hideShowBtn.setText(bShowPwd ? getResources().getString(R.string.hide_pass) : getResources().getString(R.string.show_pass));
                break;
            case  R.id.mail_close:
                mMailEdit.setText("");
                break;
            case R.id.fast_register:
                intent = new Intent(this,RegisterLoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnAccountAuthListener.class, this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String mMail = mMailEdit.getText().toString().trim();
        String mMailPass = mPasswordEdit.getText().toString().trim();
        boolean isEmail = isEmailAddress(mMail);
        if (isEmail && (mMailPass.length() > 5 && mMailPass.length() < 21) ){
            mLoginBtn.setEnabled(true);
        }else {
            mLoginBtn.setEnabled(false);
        }
        if (mMail.length() > 0){
            mMailClose.setVisibility(View.VISIBLE);
        }else {
            mMailClose.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public static boolean isEmailAddress(String email) {
        Pattern p = Pattern.compile(regEx_email);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private void dialogShow() {
        loginDialog = new CommitDialog(this);
        loginDialog.show();
        loginDialog.setStatusText(getString(R.string.dialog_commit_text));
    }

    @Override
    public void onLoginSuccess(Business business) {
        if (loginDialog != null) loginDialog.dismiss();
        int code = business.getCode();
        switch (code) {
        case ErrorCode.SUCCESS:
            MainActivity.actionStartMain(this);
            break;
        case ErrorCode.OAUTH2_INVALID_REQUEST:
            ErmuApplication.toast("oauth2_invalid_request");
            break;
        case ErrorCode.OAUTH2_UNAUTHORIZED_CLIENT:
            ErmuApplication.toast("oauth2_unauthorized_client");
            break;
        case ErrorCode.OAUTH2_REDIRECT_URI_MISMATCH:
            ErmuApplication.toast("oauth2_redirect_uri_mismatch");
            break;
        case ErrorCode.OAUTH2_ACCESS_DENIED:
            ErmuApplication.toast("oauth2_access_denied");
            break;
        case ErrorCode.OAUTH2_UNSUPPORTED_RESPONSE_TYPE:
            ErmuApplication.toast("oauth2_unsupported_response_type");
            break;
        case ErrorCode.OAUTH2_INVALID_SCOPE:
            ErmuApplication.toast("oauth2_invalid_scope");
            break;
        case ErrorCode.OAUTH2_INVALID_GRANT:
            ErmuApplication.toast("oauth2_invalid_grant");
            break;
        case ErrorCode.OAUTH2_UNSUPPORTED_GRANT_TYPE:
            ErmuApplication.toast("oauth2_unsupported_grant_type");
            break;
        case ErrorCode.OAUTH2_INSUFFICIENT_SCOPE:
            ErmuApplication.toast("oauth2_insufficient_scope");
            break;
        case ErrorCode.REFRESH_TOKEN_INVALID:
            ErmuApplication.toast("refresh_token_invalid");
            break;
        default:
            String message = business.getMessage();
            ErmuApplication.toast("登录失败"+message);
            break;
        }
    }

}
