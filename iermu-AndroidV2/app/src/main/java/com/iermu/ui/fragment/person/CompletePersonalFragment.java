package com.iermu.ui.fragment.person;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.listener.OnCompleteUserInfoListener;
import com.iermu.client.listener.OnUserInfoListener;
import com.iermu.client.model.Account;
import com.iermu.client.model.Business;
import com.iermu.client.model.UserInfo;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommitDialog;
import com.lib.pulltozoomview.PullToZoomScrollViewEx;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xjy on 15/11/27.
 */
public class CompletePersonalFragment extends BaseFragment implements TextWatcher, OnCompleteUserInfoListener, View.OnFocusChangeListener {

    private static final String INFO_NAME = "name";

    @ViewInject(R.id.input_mail)        EditText inputMail;
    @ViewInject(R.id.input_password)    EditText inputPassword;
    @ViewInject(R.id.input_nickname)    EditText inputNickName;
    @ViewInject(R.id.hide_or_show_btn)  TextView hideShowBtn;
    @ViewInject(R.id.close_perfect_person)ImageView closePerfectPerson;
    @ViewInject(R.id.save_perfect_person)Button savePerfectPerson;
    @ViewInject(R.id.img_photo)         CircleImageView photo;
    @ViewInject(R.id.mail_close)        ImageButton mMailClose;
    @ViewInject(R.id.nickname_close)    ImageButton mNameClose;

    private CommitDialog dialog;
    private String symbol = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……& ;*（）——+|{}【】‘；：”“’》《---。，、？|-]";
    private String tmp = "";
    private int count = 0;
    private String mail;
    private String password;
    private String nickname;
    private final static String regEx_email = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
       hideActionBar();
    }

    public static Fragment actionInstance(FragmentActivity activity, String username){
        CompletePersonalFragment fragment = new CompletePersonalFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INFO_NAME, username);
        fragment.setArguments(bundle);
        return  fragment;
    }
    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_person,container,false);
        ViewHelper.inject(this, view);

//        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
//        int mScreenWidth = localDisplayMetrics.widthPixels;
//        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
//        scrollView.setHeaderLayoutParams(localObject);
        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.registerListener(OnCompleteUserInfoListener.class, this);
        initView();
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    private void initView() {
        Bundle bundle = getArguments();
        String name = bundle.getString(INFO_NAME);
        inputNickName.setText(name);
        inputNickName.setSelection(name.length());
        inputMail.addTextChangedListener(this);
        inputPassword.addTextChangedListener(this);

        inputMail.setOnFocusChangeListener(this);
        inputNickName.setOnFocusChangeListener(this);

        inputPassword.setTag(1);
        Account account = AccountWrapper.queryAccount();
        if (account != null) {
            refreshView(account.getAvatar());
        }
        inputNickName.addTextChangedListener(this);
    }

    @OnClick(value = {R.id.hide_or_show_btn,R.id.close_perfect_person,R.id.save_perfect_person, R.id.mail_close, R.id.nickname_close})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.hide_or_show_btn:
                boolean bShowPwd = Integer.parseInt(inputPassword.getTag().toString()) == 1;
                inputPassword.setInputType(bShowPwd ?
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputPassword.setTag(bShowPwd ? 0 : 1);
                hideShowBtn.setText(bShowPwd ? getResources().getString(R.string.hide_pass) : getResources().getString(R.string.show_pass));
                String mPassword = inputPassword.getText().toString().trim();
                inputPassword.setSelection(mPassword.length());
                break;
            case R.id.close_perfect_person:
                hideSoft();
                popBackStack();
                break;
            case R.id.save_perfect_person:
                hideSoft();
                if (Util.isNetworkConn(getActivity())) {
                    mail = inputMail.getText().toString().trim();
                    password = inputPassword.getText().toString().trim();
                    nickname = inputNickName.getText().toString().trim();
                    String nameBegin = nickname.substring(0,1);
                    if (!isEmailAddress(mail)) {
                        customToast(getString(R.string.mail_error));
                    } else if (TextUtils.isEmpty(password)) {
                        customToast(getString(R.string.mail_password));
                    } else if (passMatch(password)) {
                        customToast(getString(R.string.no_chinese));
                    } else if (isBegin(nameBegin) || symbol.contains(nameBegin)){
                        customToast(getString(R.string.username_regular));
                    } else {
                        showDialog();
                        ErmuBusiness.getAccountAuthBusiness().completeUserInfo(nickname, mail, password);
                    }
                } else {
                    customToast(getResources().getString(R.string.no_network));
                }
                break;
            case R.id.mail_close:
                inputMail.setText("");
                break;
            case R.id.nickname_close:
                inputNickName.setText("");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ErmuBusiness.getAccountAuthBusiness().unRegisterListener(OnCompleteUserInfoListener.class, this);
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
        dialog = new CommitDialog(getActivity());
        dialog.show();
        dialog.setStatusText(getResources().getString(R.string.dialog_commit_text));
    }

    private void customToast(String str) {
        Toast toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static boolean isBegin(String nickName){
        String regex=".*[\\d-].*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickName);
        return matcher.find();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int sum) {
        nickname = inputNickName.getText().toString().trim();
        inputNickName.setSelection(nickname.length());
    }

    @Override
    public void afterTextChanged(Editable s) {
        mail = inputMail.getText().toString();
        password = inputPassword.getText().toString();
        nickname = inputNickName.getText().toString();

        ShowHide(mail, mMailClose);
        ShowHide(nickname, mNameClose);

        symbolMatch(nickname);
        charLength(nickname);

        boolean isMail = isEmailAddress(mail);
        if (isMail && (password.length() > 5 && password.length() < 21)){
            savePerfectPerson.setEnabled(true);
            savePerfectPerson.setFocusable(true);
        }else {
            savePerfectPerson.setEnabled(false);
            savePerfectPerson.setFocusable(false);
        }
        if (count < 4 || count > 20) {
            savePerfectPerson.setEnabled(false);
            savePerfectPerson.setFocusable(false);
        } else {
            savePerfectPerson.setEnabled(true);
            savePerfectPerson.setFocusable(true);
        }

    }

    public static boolean isEmailAddress(String email) {
        Pattern p = Pattern.compile(regEx_email);
        Matcher m = p.matcher(email);
        return m.matches();
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
        inputNickName.setText(tmp);
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
                inputNickName.setText(str.substring(0, i));
                break;
            }
        }
    }

    private void refreshView(String avatar) {
        Picasso.with(getActivity())
                .load(avatar)
//                .resize(photo.getWidth(), photo.getHeight())
//                .centerCrop()
                .placeholder(R.drawable.avator_img)
                .into(photo);
    }
    private void hideSoft(){
        InputUtil.hideSoftInput(getActivity(),inputMail );
        InputUtil.hideSoftInput(getActivity(),inputNickName);
        InputUtil.hideSoftInput(getActivity(),inputPassword);
    }

    private void ShowHide(String str, ImageView img) {
        if (str.length() > 0) {
            img.setVisibility(View.VISIBLE);
        } else {
            img.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPerfectUserInfo(Business business) {
        if (dialog != null) dialog.dismiss();
        int code = business.getCode();
        switch (code) {
            case BusinessCode.SUCCESS:
                Fragment fragment = UserInfoFragment.actionInstance(getActivity());
                super.addJumpToBackStack(fragment);
                break;
            case BusinessCode.USER_PROFILE_ALREADY_COMPLETED:
                ErmuApplication.toast(getString(R.string.no_complete_userinfo));
                break;
            case BusinessCode.USER_PROFILE_COMPLETE_FAILED:
                ErmuApplication.toast(getString(R.string.complete_failed));
                break;
            case BusinessCode.USER_CHECK_USERNAME_FAILED:
            case BusinessCode.USER_USERNAME_BADWORD:
                customToast(getString(R.string.nickname_badword));
                break;
            case BusinessCode.USER_USERNAME_EXISTS:
                customToast(getString(R.string.nickname_exits));
                break;
            case BusinessCode.USER_EMAIL_ACCESS_ILLEGAL:
                customToast(getString(R.string.mail_access_illegal));
                break;
            case BusinessCode.USER_EMAIL_EXSITS:
                customToast(getString(R.string.mail_exits));
                break;
            case BusinessCode.UPDATE_USERNAME_FAILED:
                ErmuApplication.toast(getString(R.string.update_name_failed));
                break;
            default:
                ErmuApplication.toast(getString(R.string.complete_failed));
                break;

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId())  {
            case R.id.input_mail:
                if(hasFocus) {
                    mMailClose.setVisibility(View.VISIBLE);
                    mNameClose.setVisibility(View.GONE);
                }
                break;
            case R.id.input_nickname:
                if(hasFocus) {
                    mMailClose.setVisibility(View.GONE);
                    mNameClose.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
