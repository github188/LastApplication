package com.iermu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.listener.OnAccountAuthListener;
import com.iermu.client.listener.OnRegisterListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.util.LanguageUtil;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommitDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * Created by xjy on 15/11/29.
 */

public class LoginiActivity extends BaseActivity implements View.OnClickListener, TextWatcher, OnAccountAuthListener, OnRegisterListener, Animation.AnimationListener {

    private static final String INTENT_SHAREAUTH_URL = "grant";
    private static final String UPDATE_PASSWORD = "password";
    @ViewInject(R.id.login_baidu)           RelativeLayout loginBaidu;
    @ViewInject(R.id.have_look)             LinearLayout haveLook;
    @ViewInject(R.id.mail_login)            Button mailLogin;
    @ViewInject(R.id.fast_register)         Button fastRe;
    @ViewInject(R.id.image)                 ImageView loginBgImg;
    @ViewInject(R.id.mail_login_two)        RelativeLayout loginBaiduMail;
    @ViewInject(R.id.login_first)           RelativeLayout loginFirst;
    @ViewInject(R.id.register_login_)       RelativeLayout registerLogin;
    @ViewInject(R.id.login_btn)             Button mLoginBtn;
    @ViewInject(R.id.login_error_txt)       TextView mLoginErrorTxt;
    @ViewInject(R.id.register_error_txt)    TextView mRegisterErrorTxt;
    @ViewInject(R.id.input_mail)            EditText mMailEdit;
    @ViewInject(R.id.hide_or_show_btn)      TextView hideShowBtn;
    @ViewInject(R.id.input_mail_password)   EditText mPasswordEdit;
    @ViewInject(R.id.mail_close)            ImageView mMailClose;
    @ViewInject(R.id.fast_register)         Button fastRegister;
    @ViewInject(R.id.mail_edit)             EditText mMailInput;
    @ViewInject(R.id.nickname_edit)         EditText mNicknameEdit;
    @ViewInject(R.id.password_edit)         EditText mPasswordInput;
    @ViewInject(R.id.hide_or_show_btn_register) TextView hideShowPass;
    @ViewInject(R.id.login_btn_register)    Button loginBtn;
    @ViewInject(R.id.mail_close_register)   ImageView mRegisterClose;
    @ViewInject(R.id.user_agreement)        TextView agreementUser;
    @ViewInject(R.id.forget_password)       Button mForgetPassword;
    @ViewInject(R.id.mail_yoyo_login)       RelativeLayout mailYoYoLogin;
    @ViewInject(R.id.register_yoyo_login)   RelativeLayout registerYoYoLogin;

    private String mail;
    private String password;
    private String nickName;
    private int a = 0;//标记
    private int mBigHeight;
    private int mBigWidth;
    private Animation leftAnimation;
    private Animation rightAnimation;
    private Animation centerAnimation;
    private CommitDialog loginDialog;
    private static int imgWidth = 1600;
    private  Animation animation1;
    String rePass;

    private final static String regEx_email = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    private String symbol = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……& ;*（）——+|{}【】‘；：”“’》《---。 ，、？|-]";
    private String tmp = "";
    String reNick;
    private int count = 0;
    private SweetAlertDialog updatePasswordDialog;

    public static void actionStartLogin(Context ctx, boolean isUpdate) {
        Intent intent = new Intent(ctx, LoginiActivity.class);
        intent.putExtra(UPDATE_PASSWORD, isUpdate);
        ctx.startActivity(intent);
    }

    /**
     * 接受邀请, 需要先登录账号
     * @param ctx
     * @param shareAuthUrl
     */
    public static void actionShareAuth(Context ctx, String shareAuthUrl) {
        Intent intent = new Intent(ctx, LoginiActivity.class);
        intent.putExtra(INTENT_SHAREAUTH_URL, shareAuthUrl);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_logni);
        ViewHelper.inject(this);

        Intent intent = getIntent();
        boolean isUpdate = intent.getBooleanExtra(UPDATE_PASSWORD, false);
        if (isUpdate) showUpdatePasswordDialog();

//        startAnimation();
        ErmuBusiness.getAccountAuthBusiness().registerListener(OnRegisterListener.class, this);
        mLoginBtn.setTextColor(0x3f00acef);
        loginBtn.setTextColor(0x3f00acef);
        mPasswordEdit.setTag(0);
        mMailEdit.addTextChangedListener(this);
        mPasswordEdit.addTextChangedListener(this);
        haveLook.setOnClickListener(this);
        loginBaidu.setOnClickListener(this);
        mailLogin.setOnClickListener(this);
        fastRe.setOnClickListener(this);
        String passMail = mPasswordEdit.getText().toString().trim();
        mPasswordEdit.setSelection(passMail.length());
        setSpanPublicLive();
        mPasswordInput.setTag(0);
        mMailInput.addTextChangedListener(this);
        mPasswordInput.addTextChangedListener(this);
        mNicknameEdit.addTextChangedListener(this);
        String passRegister = mPasswordInput.getText().toString();
        mPasswordInput.setSelection(passRegister.length());
        mNicknameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
    }

    private void setSpanPublicLive() {
        String agreement = getResources().getString(R.string.user_agreement);
        String protocol = getResources().getString(R.string.user_agreement_txt);
        int strLength = agreement.length();
        int proLength = protocol.length();
        SpannableStringBuilder style = new SpannableStringBuilder(agreement);
        TextViewURLSpan myURLSpan = new TextViewURLSpan();
        style.setSpan(myURLSpan,strLength - proLength, strLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        agreementUser.setText(style);
        agreementUser.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ErmuBusiness.getAccountAuthBusiness().registerListener(OnAccountAuthListener.class, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnAccountAuthListener.class, this);
    }

    @OnClick(value = {R.id.login_btn, R.id.mail_login_back, R.id.login_baidu_mail, R.id.hide_or_show_btn, R.id.mail_close, R.id.fast_register_mail, R.id.login_baidu, R.id.mail_login
            , R.id.have_look, R.id.fast_register, R.id.register_login_back, R.id.mail_close_register, R.id.hide_or_show_btn_register, R.id.login_btn_register, R.id.forget_password})//R.id.agreement_user,
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_baidu:
                if (Util.isNetworkConn(this)) {
                    Intent  intentLogin = getIntent();
                    if (intentLogin.hasExtra(INTENT_SHAREAUTH_URL)) {
                        String extra = intentLogin.getStringExtra(INTENT_SHAREAUTH_URL);
                        BaiDuLoginActivity.actionShareAuth(this, extra);
                    } else {
                        BaiDuLoginActivity.actionStartBaiduLogin(this, false);
                    }
                } else {
                    ErmuApplication.toast(getResources().getString(R.string.check_network));
                }
                break;
            case R.id.mail_login:
                loginFirst.clearAnimation();
                loginFirst.setVisibility(View.INVISIBLE);
                loginBaiduMail.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(250).playOn(mailYoYoLogin);
                break;
            case R.id.have_look:
                PubCamListActivity.actionPubCamList(this);
                break;
            case R.id.fast_register_mail:
                loginFirst.clearAnimation();
                a = 1;
                loginBaiduMail.setVisibility(View.INVISIBLE);
                registerLogin.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(250).playOn(registerYoYoLogin);
                break;
            case R.id.login_btn://TODO 邮箱登录：判断邮箱和密码
                InputUtil.hideSoftInput(this, mMailEdit);
                InputUtil.hideSoftInput(this, mPasswordEdit);
                mLoginErrorTxt.setVisibility(View.VISIBLE);
                mRegisterErrorTxt.setVisibility(View.VISIBLE);
                if (Util.isNetworkConn(this)) {
                    showDialog();
                    mail = mMailEdit.getText().toString().trim();
                    password = mPasswordEdit.getText().toString().trim();
                    IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
                    business.registerAccount(mail, password);
                } else {
                    ErmuApplication.toast(getResources().getString(R.string.check_network));
                }
                break;
            case R.id.mail_login_back:
                a  = 3;
                InputUtil.hideSoftInput(this, mPasswordEdit);
                InputUtil.hideSoftInput(this, mMailEdit);
                mLoginErrorTxt.setVisibility(View.GONE);
                mRegisterErrorTxt.setVisibility(View.GONE);
                mMailEdit.setText("");
                mPasswordEdit.setText("");
                exitAnimation(mailYoYoLogin);
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
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD:
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mPasswordEdit.setTag(bShowPwd ? 0 : 1);
                hideShowBtn.setText(bShowPwd ? getResources().getString(R.string.show_pass) : getResources().getString(R.string.hide_pass));
                String passMail = mPasswordEdit.getText().toString().trim();
                mPasswordEdit.setSelection(passMail.length());
                break;
            case R.id.mail_close:
                mMailEdit.setText("");
                break;
            case R.id.fast_register:
                a = 2;
                loginFirst.clearAnimation();
                loginFirst.setVisibility(View.INVISIBLE);
                registerLogin.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(250).playOn(registerYoYoLogin);
                break;
            case R.id.register_login_back:
                InputUtil.hideSoftInput(this, mMailInput);
                InputUtil.hideSoftInput(this, mPasswordInput);
                InputUtil.hideSoftInput(this, mNicknameEdit);
                mLoginErrorTxt.setVisibility(View.GONE);
                mRegisterErrorTxt.setVisibility(View.GONE);
                mMailInput.setText("");
                mNicknameEdit.setText("");
                mPasswordInput.setText("");
                if (a == 2){
                    exitAnimation(registerYoYoLogin);
                }else if (a == 1){
                    exitAnimation(registerYoYoLogin);
                }
                break;
            case R.id.hide_or_show_btn_register:
                boolean bShowPwdRe = Integer.parseInt(mPasswordInput.getTag().toString()) == 1;
                mPasswordInput.setInputType(bShowPwdRe ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mPasswordInput.setTag(bShowPwdRe ? 0 : 1);
                hideShowPass.setText(bShowPwdRe ? getResources().getString(R.string.show_pass) : getResources().getString(R.string.hide_pass));
                String passRegister = mPasswordInput.getText().toString();
                mPasswordInput.setSelection(passRegister.length());
                break;
            case R.id.login_btn_register://TODO  注册邮箱判断是否输入正确
                InputUtil.hideSoftInput(this, mMailInput);
                InputUtil.hideSoftInput(this, mPasswordInput);
                InputUtil.hideSoftInput(this, mNicknameEdit);
                mLoginErrorTxt.setVisibility(View.VISIBLE);
                mRegisterErrorTxt.setVisibility(View.VISIBLE);
                if (Util.isNetworkConn(this)) {
                    mail = mMailInput.getText().toString().trim();
                    password = mPasswordInput.getText().toString().trim();
                    nickName = mNicknameEdit.getText().toString();
                    if (!TextUtils.isEmpty(nickName)) {
                        charLength(nickName);
                        if (count > 0 && count < 4 || count > 20) {
                            customToast(getString(R.string.nick_name_length));
                            return;
                        }
                    }
//                    nickName = mNicknameEdit.getText().toString().trim();
                    String nameBegin = nickName.substring(0,1);
                    if (isBegin(nameBegin) || symbol.contains(nameBegin)){
                        customToast(getString(R.string.username_regular));
                    } else if (!TextUtils.isEmpty(password) && passMatch(password)) {
                        customToast(getString(R.string.no_chinese));
                    } else {
                        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
                        business.emailRegister(nickName, password, mail);
                        showDialog();
                    }
                } else {
                    ErmuApplication.toast(getResources().getString(R.string.check_network));
                }
                break;
            case R.id.mail_close_register:
                mMailInput.setText("");
                break;
//            case R.id.agreement_user:
//                WebActivity.actionStartWeb(this,WebActivity.USER_AGREEMENT);
//                break;
            case R.id.forget_password:
                WebActivity.actionStartWeb(this,WebActivity.FIND_PASSWORD);
                break;
        }
    }

    private void showUpdatePasswordDialog() {
        updatePasswordDialog = new SweetAlertDialog(this);
        updatePasswordDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
        updatePasswordDialog.setContentText(getString(R.string.password_update));
        updatePasswordDialog.setConfirmText(getResources().getString(R.string.dialog_confirm));
        updatePasswordDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        updatePasswordDialog.show();
    }

    private boolean passMatch(String password) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(password);
        while (m.find()) {
            return true;
        }
        return false;
    }

    private void showDialog() {
        loginDialog = new CommitDialog(this);
        loginDialog.show();
        loginDialog.setStatusText(getResources().getString(R.string.dialog_commit_text));
    }

    private void dialogDismiss() {
        if (loginDialog != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loginDialog.dismiss();
                }
            }, 0);
        }
    }

    private void startAnimation() {
        int width = Util.getDisplayWidth(this);
        if (imgWidth <= width) return;
        int marginLeft = (imgWidth - width)/2;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(-marginLeft, 0, -marginLeft, 0);
        loginBgImg.setLayoutParams(params);

        centerAnimation = new TranslateAnimation(0, -marginLeft,0, 0);
        centerAnimation.setDuration(25000);
        centerAnimation.setFillAfter(true);
        loginBgImg.startAnimation(centerAnimation);

        leftAnimation = new TranslateAnimation(-marginLeft, marginLeft,0, 0);
        leftAnimation.setDuration(25000);
        leftAnimation.setFillAfter(true);
        loginBgImg.startAnimation(leftAnimation);

        rightAnimation = new TranslateAnimation(marginLeft, -marginLeft,0, 0);
        rightAnimation.setDuration(25000);
        rightAnimation.setFillAfter(true);
        loginBgImg.startAnimation(rightAnimation);

        centerAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                loginBgImg.startAnimation(leftAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        leftAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                loginBgImg.startAnimation(rightAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        rightAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                loginBgImg.startAnimation(leftAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void stopAnimation() {
        if (leftAnimation != null) leftAnimation.cancel();
        if (rightAnimation != null) rightAnimation.cancel();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        tmp = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String nickName = mNicknameEdit.getText().toString();
//        mNicknameEdit.setSelection(nickName.length());
    }

    @Override
    public void afterTextChanged(Editable s) {
        String nickName = mNicknameEdit.getText().toString();
//        symbolMatch(nickName);
//        charLength(nickName);

        String mMail = mMailEdit.getText().toString().trim();
        String mMailPass = mPasswordEdit.getText().toString().trim();
        if (mMail.length() == 0 && mMailPass.length() == 0) {
            mLoginErrorTxt.setVisibility(View.GONE);
        }
        boolean isEmail = isEmailAddress(mMail);
        if (isEmail && (mMailPass.length() > 5 && mMailPass.length() < 21)) {
            mLoginBtn.setEnabled(true);
            mLoginBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            mLoginBtn.setEnabled(false);
            mLoginBtn.setTextColor(0x3f00acef);
        }
        if (mMail.length() > 0) {
            mMailClose.setVisibility(View.VISIBLE);
        } else {
            mMailClose.setVisibility(View.INVISIBLE);
        }

        String reMail = mMailInput.getText().toString().trim();
        String rePass = mPasswordInput.getText().toString();
        if (reMail.length() ==0 && rePass.length() == 0 && nickName.length() == 0) {
            mRegisterErrorTxt.setVisibility(View.GONE);
        }
        boolean isAddress = isEmailAddress(reMail);
        if (isAddress && (rePass.length() >= 6 && rePass.length() <= 20) && nickName.length() >= 2) {// && ( count>= 4 && count <= 20)
            loginBtn.setEnabled(true);
            loginBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            loginBtn.setEnabled(false);
            loginBtn.setTextColor(0x3f00acef);
        }
        if (reMail.length() > 0) {
            mRegisterClose.setVisibility(View.VISIBLE);
        } else {
            mRegisterClose.setVisibility(View.INVISIBLE);
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
        mNicknameEdit.setText(tmp);
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
//                mNicknameEdit.setText(str.substring(0, i));
//                customToast(getString(R.string.nick_name_length));
                break;
            }
        }
    }

    private void customToast(String str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAnimation();
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnAccountAuthListener.class, this);
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnRegisterListener.class, this);
    }

    public static boolean isEmailAddress(String email) {
        Pattern p = Pattern.compile(regEx_email);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isBegin(String nickName){
        String regex=".*[\\d-].*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickName);
        return matcher.find();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (registerLogin.getVisibility() == View.VISIBLE) {
                mLoginErrorTxt.setVisibility(View.GONE);
                mRegisterErrorTxt.setVisibility(View.GONE);
                mMailInput.setText("");
                mNicknameEdit.setText("");
                mPasswordInput.setText("");
                if (a==1){
                    exitAnimation(registerYoYoLogin);
                }else if (a== 2){
                    exitAnimation(registerYoYoLogin);
                }
            } else if (loginBaiduMail.getVisibility() == View.VISIBLE) {
                mLoginErrorTxt.setVisibility(View.GONE);
                mRegisterErrorTxt.setVisibility(View.GONE);
                mMailEdit.setText("");
                mPasswordEdit.setText("");
                Animation animation2 = AnimationUtils.loadAnimation(this,R.anim.dialog_exit);
                mailYoYoLogin.startAnimation(animation2);
                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loginBaiduMail.setVisibility(View.INVISIBLE);
                        loginFirst.setVisibility(View.VISIBLE);
                        setCenterAnimation(loginFirst);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRegister(Business business) {
        int code = business.getCode();
        switch (code) {
            case BusinessCode.SUCCESS:
                IAccountAuthBusiness business1 = ErmuBusiness.getAccountAuthBusiness();
                business1.registerAccount(mail, password);
                break;
            case BusinessCode.USER_CHECK_USERNAME_FAILED:
            case BusinessCode.USER_USERNAME_BADWORD:
                if (loginDialog != null) loginDialog.dismiss();
//                customToast(getString(R.string.nickname_badword));
                mRegisterErrorTxt.setText(getResources().getString(R.string.nickname_badword));
                break;
            case BusinessCode.USER_USERNAME_EXISTS:
                if (loginDialog != null) loginDialog.dismiss();
//                customToast(getString(R.string.nickname_exits));
                mRegisterErrorTxt.setText(getResources().getString(R.string.nickname_exits));
                break;
            case BusinessCode.USER_EMAIL_ACCESS_ILLEGAL:
                if (loginDialog != null) loginDialog.dismiss();
//                customToast(getString(R.string.mail_access_illegal));
                mRegisterErrorTxt.setText(getResources().getString(R.string.mail_access_illegal));
                break;
            case BusinessCode.USER_EMAIL_EXSITS:
                if (loginDialog != null) loginDialog.dismiss();
//                customToast(getString(R.string.mail_exits));
                mRegisterErrorTxt.setText(getResources().getString(R.string.mail_exits));
                break;
            default:
                if (loginDialog != null) loginDialog.dismiss();
//                customToast(getString(R.string.register_faild)+":"+code);
                mRegisterErrorTxt.setText(getResources().getString(R.string.register_faild)+":"+code);
                break;
        }
    }

    @Override
    public void onLoginSuccess(Business business) {
        dialogDismiss();
        int code = business.getCode();
        switch (code) {
            case BusinessCode.SUCCESS:
                Intent intent = getIntent();
                if (intent.hasExtra(INTENT_SHAREAUTH_URL)) {
                    String extra = intent.getStringExtra(INTENT_SHAREAUTH_URL);
                    MainActivity.actionShareAuth(this, extra);
                } else {
                    MainActivity.actionStartMain(this);
                }
                break;
            case BusinessCode.OAUTH2_INVALID_GRANT:
            case BusinessCode.OAUTH2_ERROR_USER_NOT_EXIST:
            case BusinessCode.OAUTH2_ERROR_USER_PASSWORD:
//                customToast(getString(R.string.account_password_error));
                mLoginErrorTxt.setText(getResources().getString(R.string.account_password_error));
                break;
            default:
//                customToast(getString(R.string.login_fail_try_again)+":"+code);
                mLoginErrorTxt.setText(getResources().getString(R.string.account_password_error));
                break;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (a == 3){
            loginBaiduMail.setVisibility(View.INVISIBLE);
            loginFirst.setVisibility(View.VISIBLE);
            setCenterAnimation(loginFirst);
        }else if (a == 2){
            registerLogin.setVisibility(View.INVISIBLE);
            loginFirst.setVisibility(View.VISIBLE);
            setCenterAnimation(loginFirst);
        }else if (a == 1){
            registerLogin.setVisibility(View.INVISIBLE);
            loginBaiduMail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    private void exitAnimation(RelativeLayout re){
        Animation animationExit = AnimationUtils.loadAnimation(this,R.anim.dialog_exit);
        re.startAnimation(animationExit);
        animationExit.setAnimationListener(this);
    }
    private void setCenterAnimation(RelativeLayout re){
         animation1 = AnimationUtils.loadAnimation(this,R.anim.login_bg_enter);
        re.startAnimation(animation1);
    }

    private class TextViewURLSpan extends ClickableSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.mycam_talk));
            ds.setUnderlineText(true); //加上下划线
        }
        @Override
        public void onClick(View widget) {//点击事件
            WebActivity.actionStartWeb(LoginiActivity.this,WebActivity.USER_AGREEMENT);
        }
    }

}

