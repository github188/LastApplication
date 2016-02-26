package com.iermu.ui.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.business.api.response.ErrorCode;
import com.iermu.client.listener.OnAccountAuthListener;
import com.iermu.client.listener.OnRegisterListener;
import com.iermu.client.model.Business;
import com.iermu.client.util.Logger;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.view.CommitDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xjy on 15/11/30.
 */
public class RegisterLoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher, OnRegisterListener, OnAccountAuthListener {


    @ViewInject(R.id.mail_edit)     EditText mMailEdit;
    @ViewInject(R.id.nickname_edit) EditText mNicknameEdit;
    @ViewInject(R.id.password_edit) EditText mPasswordEdit;
    @ViewInject(R.id.hide_or_show_btn)  TextView hideShowPass;
    @ViewInject(R.id.login_btn)     Button loginBtn;
    @ViewInject(R.id.mail_close)    ImageView mMailClose;
    @ViewInject(R.id.agreement_user)TextView agreementUser;

    private CommitDialog loginDialog;
    private String mail;
    private String password;
    private String nickName;

    private final static String regEx_email = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_login);
        ViewHelper.inject(this);
        agreementUser.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mPasswordEdit.setTag(0);
        mMailEdit.addTextChangedListener(this);
        mPasswordEdit.addTextChangedListener(this);
        mNicknameEdit.addTextChangedListener(this);
    }

    @OnClick(value = {R.id.register_login_back, R.id.mail_close, R.id.hide_or_show_btn, R.id.login_btn, R.id.agreement_user})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_login_back:
                finish();
                break;
            case R.id.hide_or_show_btn:
                boolean bShowPwd = Integer.parseInt(mPasswordEdit.getTag().toString()) == 1;
                mPasswordEdit.setInputType(bShowPwd ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mPasswordEdit.setTag(bShowPwd ? 0 : 1);
                hideShowPass.setText(bShowPwd ? getResources().getString(R.string.hide_pass) : getResources().getString(R.string.show_pass));
                break;
            case R.id.login_btn:
                dialogShow();
                InputUtil.hideSoftInput(this, mMailEdit);
                InputUtil.hideSoftInput(this, mPasswordEdit);
                InputUtil.hideSoftInput(this, mNicknameEdit);
                mail = mMailEdit.getText().toString().trim();
                password = mPasswordEdit.getText().toString().trim();
                nickName = mNicknameEdit.getText().toString().trim();
                if ((nickName.length() < 2) || (nickName.length() > 20)) {
                    customToast(getString(R.string.register_input_nickname));
                } else if (password.length() < 6 || password.length() > 20) {
                    customToast(getString(R.string.person_password));
                } else {
                    IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
                    business.emailRegister(nickName, password, mail);
                    business.registerListener(OnRegisterListener.class, this);
                }
                break;
            case R.id.mail_close:
                mMailEdit.setText("");
                break;
            case R.id.agreement_user:
                //TODO 用户协议
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnRegisterListener.class, this);
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnAccountAuthListener.class, this);
    }

    private void customToast(String str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String reMail = mMailEdit.getText().toString().trim();
        String rePass = mPasswordEdit.getText().toString().trim();
        String reNick = mNicknameEdit.getText().toString().trim();
        boolean isAddress = isEmailAddress(reMail);
        if (isAddress && (rePass.length() >= 6 && rePass.length() <= 20) && (reNick.length() >= 2 && reNick.length() <= 20)) {
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setEnabled(false);
        }
        if (reMail.length() > 0){
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
    public void onRegister(Business business) {
        int code = business.getCode();
        switch (code) {
        case ErrorCode.SUCCESS:
            Logger.i("注册成功");
            IAccountAuthBusiness business1 = ErmuBusiness.getAccountAuthBusiness();
            business1.registerAccount(mail, password);
            business1.registerListener(OnAccountAuthListener.class, this);
            break;
        case ErrorCode.OAUTH2_INVALID_CLIENT:
//            ErmuApplication.toast(getString(R.string.oauth_invalid_client));
            break;
        case ErrorCode.USER_CHECK_USERNAME_FAILED:
            ErmuApplication.toast(getString(R.string.nickname_failed));
            break;
        case ErrorCode.USER_USERNAME_BADWORD:
            ErmuApplication.toast(getString(R.string.nickname_badword));
            break;
        case ErrorCode.USER_USERNAME_EXISTS:
            ErmuApplication.toast(getString(R.string.nickname_exits));
            break;
        case ErrorCode.USER_EMAIL_FORMAT_ILLEGAL:
//            ErmuApplication.toast(getString(R.string.mail_format_illegal));
            break;
        case ErrorCode.USER_EMAIL_ACCESS_ILLEGAL:
            ErmuApplication.toast(getString(R.string.mail_access_illegal));
            break;
        case ErrorCode.USER_EMAIL_EXSITS:
            ErmuApplication.toast(getString(R.string.mail_exits));
            break;
        case ErrorCode.USER_ADD_FAILED:
//            ErmuApplication.toast(getString(R.string.add_user_failed));
            break;
        default:
            String message = business.getMessage();
            Logger.i("注册失败:" + message);
            break;

        }
    }

    @Override
    public void onLoginSuccess(Business business) {
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
                Logger.i("登录失败" + message + code);
                break;
        }
        if (loginDialog != null) loginDialog.dismiss();
    }
}
