package com.iermu.ui.fragment.person;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.listener.OnUpdatePasswordListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.ui.activity.LoginiActivity;
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
 * Created by xjy on 15/11/26.
 */
public class ChangePasswordFragment extends BaseFragment implements  TextWatcher, View.OnFocusChangeListener, OnUpdatePasswordListener {

    @ViewInject(R.id.current_pass)      EditText currentPass;
    @ViewInject(R.id.new_pass)          EditText newPass;
//    @ViewInject(R.id.confirm_pass)      EditText confirmPass;
//    @ViewInject(R.id.actionbar_finish)  TextView actionbarFinish;
    @ViewInject(R.id.actionbar_title)   TextView actionTitle;
    @ViewInject(R.id.current_pass_close)ImageView currentPassClose;
    @ViewInject(R.id.new_pass_close)    ImageView newPassClose;
//    @ViewInject(R.id.confirm_pass_close)ImageView confirmPassClose;
    @ViewInject(R.id.show_hide_img)     ImageView showHideImg;
    @ViewInject(R.id.show_hide_text)    TextView showHideText;
    @ViewInject(R.id.title_bg)          LinearLayout titleBg;
    @ViewInject(R.id.save_perfect)      Button mSave;

    private String currentPassword;
    private String newPassword;
    private CommitDialog dialog;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_about);
    }

    public static Fragment actionInstance(FragmentActivity Activity) {
        return new ChangePasswordFragment();
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        actionTitle.setText(R.string.change_password);
//        actionbarFinish.setText(R.string.text_button);
        titleBg.setBackgroundResource(R.color.devices_help_btn_bg);
//        actionbarFinish.setTextSize(16);
//        actionbarFinish.setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_pasword, container, false);
        ViewHelper.inject(this, view);

        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.registerListener(OnUpdatePasswordListener.class, this);
        initView();
        return view;
    }

    private void initView() {
        checkInputState();
        currentPass.setOnFocusChangeListener(this);
        newPass.setOnFocusChangeListener(this);
//        confirmPass.setOnFocusChangeListener(this);

        currentPass.addTextChangedListener(this);
        newPass.addTextChangedListener(this);
//        confirmPass.addTextChangedListener(this);
        currentPass.setTag(0);
    }

    @OnClick(value = {R.id.save_perfect, R.id.actionbar_back, R.id.current_pass_close,R.id.new_pass_close,R.id.check_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_back:
                InputUtil.hideSoftInput(getActivity(), currentPass);
                InputUtil.hideSoftInput(getActivity(), newPass);
                popBackStack();
                break;
            case R.id.save_perfect:
                if (Util.isNetworkConn(getActivity())) {
                    currentPassword = this.currentPass.getText().toString().trim();
                    newPassword = newPass.getText().toString().trim();
//                    confirmPassword = confirmPass.getText().toString().trim();
//                    InputUtil.hideSoftInput(getActivity(), confirmPass);
                    InputUtil.hideSoftInput(getActivity(), this.currentPass);
                    InputUtil.hideSoftInput(getActivity(), newPass);
                    if (!TextUtils.isEmpty(newPassword) && passMatch(newPassword)){
                        customToast(getString(R.string.no_chinese));
                    }
                    else {
                        showDialog();
                        ErmuBusiness.getAccountAuthBusiness().updatePassword(currentPassword, newPassword);
                    }

                } else {
                    customToast(getResources().getString(R.string.no_network));
                }
                break;
            case R.id.current_pass_close:
                deleteInputpassword(currentPass);
                break;
            case R.id.new_pass_close:
                deleteInputpassword(newPass);
                break;
//            case R.id.confirm_pass_close:
//                deleteInputpassword(confirmPass);
//                break;
            case R.id.check_btn:
                boolean bShowPwd = Integer.parseInt(currentPass.getTag().toString()) == 1;
                setShowState(currentPass, bShowPwd);
                setShowState(newPass, bShowPwd);
                currentPass.setTag(bShowPwd ? 0 : 1);
                showHideText.setText(bShowPwd ? getResources().getString(R.string.show_pass) : getResources().getString(R.string.hide_pass));
                showHideImg.setImageResource(bShowPwd ? R.drawable.show_password:R.drawable.hide_password);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        InputUtil.hideSoftInput(getActivity(), confirmPass);
        InputUtil.hideSoftInput(getActivity(), currentPass);
        InputUtil.hideSoftInput(getActivity(), newPass);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.unRegisterListener(OnUpdatePasswordListener.class, this);
    }

    private void setShowState(EditText editText, boolean isShow) {
        editText.setInputType(isShow ?
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void customToast(String str) {
        Toast toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void checkInputState() {
        mSave.setEnabled(false);
        mSave.setFocusable(false);
    }

    private void showDialog() {
        dialog = new CommitDialog(getActivity());
        dialog.show();
        dialog.setStatusText(getResources().getString(R.string.dialog_commit_text));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        currentPassword = currentPass.getText().toString().trim();
        newPassword = newPass.getText().toString().trim();
//        confirmPassword = confirmPass.getText().toString().trim();
        ShowHide(currentPassword, currentPassClose);
        ShowHide(newPassword, newPassClose);
//        ShowHide(confirmPassword, confirmPassClose);
        if (TextUtils.isEmpty(currentPassword) ||TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
            mSave.setEnabled(false);
            mSave.setFocusable(false);
        } else {
            mSave.setEnabled(true);
            mSave.setFocusable(true);
        }
        isShowCloseImg();

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void isShowCloseImg() {
        boolean currFocused = currentPass.isFocused();
        boolean newPassFocused = newPass.isFocused();
//        boolean confirmFocused = confirmPass.isFocused();
        if (currFocused) {
            if (!TextUtils.isEmpty(currentPassword)) {
                currentPassClose.setVisibility(View.VISIBLE);
//                confirmPassClose.setVisibility(View.INVISIBLE);
                newPassClose.setVisibility(View.INVISIBLE);
            } else {
                currentPassClose.setVisibility(View.INVISIBLE);
//                confirmPassClose.setVisibility(View.INVISIBLE);
                newPassClose.setVisibility(View.INVISIBLE);
            }
        }
        if (newPassFocused) {
            if (!TextUtils.isEmpty(newPassword)) {
                newPassClose.setVisibility(View.VISIBLE);
                currentPassClose.setVisibility(View.INVISIBLE);
//                confirmPassClose.setVisibility(View.INVISIBLE);
            } else {
                currentPassClose.setVisibility(View.INVISIBLE);
//                confirmPassClose.setVisibility(View.INVISIBLE);
                newPassClose.setVisibility(View.INVISIBLE);
            }
        }
//        if (confirmFocused) {
//            if (!TextUtils.isEmpty(confirmPassword)) {
//                confirmPassClose.setVisibility(View.VISIBLE);
//                currentPassClose.setVisibility(View.INVISIBLE);
//                newPassClose.setVisibility(View.INVISIBLE);
//            } else {
//                currentPassClose.setVisibility(View.INVISIBLE);
//                confirmPassClose.setVisibility(View.INVISIBLE);
//                newPassClose.setVisibility(View.INVISIBLE);
//            }
//        }

    }

    /**
     * 删除密码的显示隐藏
     *
     * @param str
     * @param img
     */
    private void ShowHide(String str, ImageView img) {
        if (str.length() > 0) {
            img.setVisibility(View.VISIBLE);
        } else {
            img.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteInputpassword(EditText edi) {
        edi.setText("");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String newPa = newPass.getText().toString().trim();
//        String confirmPa = confirmPass.getText().toString().trim();
        switch (v.getId()) {
            case R.id.new_pass:
                if (!hasFocus) {
                    if (newPa.length() < 6) {
                        customToast(getString(R.string.password_lass));
                    }
                } else {
                    if (!TextUtils.isEmpty(newPassword)) {
                        newPassClose.setVisibility(View.VISIBLE);
                        currentPassClose.setVisibility(View.INVISIBLE);
//                        confirmPassClose.setVisibility(View.INVISIBLE);
                    } else {
                        currentPassClose.setVisibility(View.INVISIBLE);
//                        confirmPassClose.setVisibility(View.INVISIBLE);
                        newPassClose.setVisibility(View.INVISIBLE);
                    }
                }
                break;
//            case R.id.confirm_pass:
//                if (!hasFocus){
//                    if (!confirmPa.equals(newPa)) {
//
//                    }
//                } else {
//                    if (!TextUtils.isEmpty(confirmPassword)) {
//                        confirmPassClose.setVisibility(View.VISIBLE);
//                        currentPassClose.setVisibility(View.INVISIBLE);
//                        newPassClose.setVisibility(View.INVISIBLE);
//                    } else {
//                        currentPassClose.setVisibility(View.INVISIBLE);
//                        confirmPassClose.setVisibility(View.INVISIBLE);
//                        newPassClose.setVisibility(View.INVISIBLE);
//                    }
//                }
//                break;
            case R.id.current_pass:
                if (hasFocus) {
                    if (!TextUtils.isEmpty(currentPassword)) {
                        currentPassClose.setVisibility(View.VISIBLE);
//                        confirmPassClose.setVisibility(View.INVISIBLE);
                        newPassClose.setVisibility(View.INVISIBLE);
                    } else {
                        currentPassClose.setVisibility(View.INVISIBLE);
//                        confirmPassClose.setVisibility(View.INVISIBLE);
                        newPassClose.setVisibility(View.INVISIBLE);
                    }
                }
                break;
        }
    }

    @Override
    public void onUpdatePassword(final Business business) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) dialog.dismiss();
                int code = business.getCode();
                switch (code) {
                    case BusinessCode.SUCCESS:
                        CookieSyncManager.createInstance(getActivity());
                        CookieManager.getInstance().removeAllCookie();
                        CookieSyncManager.getInstance().sync();
                        ErmuBusiness.getAccountAuthBusiness().logout();
                        LoginiActivity.actionStartLogin(getActivity(), true);
                        getActivity().finish();
                        break;
                    case BusinessCode.PASSWORD_ERROR:
                        ErmuApplication.toast(getString(R.string.password_error));
                        break;
                    default:
                        ErmuApplication.toast(getString(R.string.update_password_failed));
                        break;
                }
            }
        }, 500);

    }

    private boolean passMatch(String password) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(password);
        while (m.find()) {
            return true;
        }
        return false;
    }
}
