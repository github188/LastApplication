package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnSetDevEmailListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.Email;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.InputUtil;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xjy on 15/6/25.
 */
public class CamEmailFragment extends BaseFragment implements View.OnClickListener, OnCamSettingListener, BaseFragment.OnCommonClickListener,OnSetDevEmailListener {

    @ViewInject(R.id.email_password)    EditText mPassword;
    @ViewInject(R.id.email_username)    EditText mName;
    @ViewInject(R.id.email_receiver)    EditText mReceiver;
    @ViewInject(R.id.email_sender)      EditText mSender;
    @ViewInject(R.id.email_chaosong)    EditText mChaosong;
    @ViewInject(R.id.email_port)        TextView mPort;
    @ViewInject(R.id.email_server)      EditText mServer;
    @ViewInject(R.id.swith_button)      SwitchButtonNew mSwitch;

    private String suffix;
    private String serverSuffix;
    CommonCommitDialog dialog;
    private boolean isOpen;
    private ICamSettingBusiness business;
    private String deviceId;
    private static final String INTENT_DEVICEID = "deviceid";

    private final static String regEx_email = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

    public static Fragment actionInstance(FragmentActivity activity) {
        return new CamEmailFragment();
    }
    public static Fragment actionInstance(FragmentActivity activity,String deviceId){
        Fragment fragment = new CamEmailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.email)
                .setCommonFinishClick(this)
                .setCommonFinish(R.string.text_button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_email, container, false);
        ViewHelper.inject(this, view);
        //mReceiver.requestFocus();
        InputUtil.showSoftInput(getActivity(), mReceiver);
        mReceiver.addTextChangedListener(watcher);
        mSwitch.setOnCheckedChangeListener(listener);

        deviceId = getArguments().getString(INTENT_DEVICEID);
        business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnCamSettingListener.class,this);
        business.registerListener(OnSetDevEmailListener.class,this);
//        business.syncCamSetting(deviceId);
        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        InputUtil.hideSoftInput(getActivity(), mReceiver);
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class,this);
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String email = mReceiver.getText().toString().trim();
            boolean isEmail = isEmailAddress(email);
            boolean focused = mReceiver.isFocused();
            if (isEmail && focused) {
                mName.setText(email);
                mSender.setText(email);
                int st = email.indexOf("@");
                suffix = email.substring(st);
                serverSuffix = email.substring(st+1);
                mServer.setText("smtp." + serverSuffix);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mPort.setText("465");
            } else {
                mPort.setText("25");
            }
            isOpen = isChecked;
        }
    };
    @Override
    public void onClick(View v) {
        InputUtil.hideSoftInput(getActivity(), mPassword);
        String password = mPassword.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String receiver = mReceiver.getText().toString().trim();
        String sender = mSender.getText().toString().trim();
        String chaosong = mChaosong.getText().toString().trim();
        String port = mPort.getText().toString().trim();
        String server = mServer.getText().toString().trim();
        boolean isSender = isEmailAddress(sender);
        boolean isReceiver = isEmailAddress(receiver);
        boolean isChaosong = isEmailAddress(chaosong);
//        if(password.length() < 6) {
//            Toast toast = Toast.makeText(getActivity(),"密码不能低于6位",Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER,0,0);
//            toast.show();
//            return;
//        }

//        password = encryptPass(password);

        if (TextUtils.isEmpty(receiver)) {
            toast(getString(R.string.receiver));
            return;
        } else {
//                isMoreEmail(receiver);
            boolean isMail = isEmailAddress(receiver);
            if (!isMail) {
                ErmuApplication.toast(getString(R.string.mail_format_error));
                return;
            }
        }
        if (TextUtils.isEmpty(port)) {
            ErmuApplication.toast(getString(R.string.port_null));
            return;
        } else if (TextUtils.isEmpty(sender)) {
            ErmuApplication.toast(getString(R.string.receiver));
            return;
        } else if (TextUtils.isEmpty(name)) {
            ErmuApplication.toast(getString(R.string.account_null));
            return;
        } else if (TextUtils.isEmpty(password)) {
            ErmuApplication.toast(getString(R.string.password_null));
            return;
        } else if ((!TextUtils.isEmpty(chaosong) && !isChaosong)) {
            ErmuApplication.toast(getString(R.string.copy_mail_format_error));
            return;
        } else if (!isSender) {
            ErmuApplication.toast(getString(R.string.send_mail_format_error));
        } else if (!isReceiver) {
            ErmuApplication.toast(getString(R.string.mail_format_error));
        }else{
            dialog = new CommonCommitDialog(getActivity());
            dialog.show();
            dialog.setStatusText(getString(R.string.dialog_commit));
            //TODO  数据
             ErmuBusiness.getCamSettingBusiness().setDevEmail(deviceId,receiver, chaosong, server, port ,sender, name, password, isOpen);

        }

    }
//    private String encryptPass(String password) {
//        Logger.i(password);
//        int m = new Random().nextInt(9);
//        int n = new Random().nextInt(9);
//        int i = (int) Math.abs(Math.random() * 900 + 100);
//        int j = (int) Math.abs(Math.random() * 9000 + 1000);
//        String var1 = Integer.toString(i);
//        String var2 = Integer.toString(j);
//
//        if (m>n) {
//            password = password.substring(0,m) + var2;
//            password = password.substring(0,n) + var1 + Integer.toString(m) + "3" + Integer.toString(n) + 4;
//        } else {
//            password = password.substring(0,n) + var1;
//            password =  password.substring(0,m) + var2 + Integer.toString(n) + "3" + Integer.toString(m) + 4;
//        }
//        Logger.i(n + "------" + m + "------" + var1 + "--------" + var2 + "-------" + password);
//        return password;
//    }
//
//    private void isMoreEmail(String text) {
//        if (text.contains("@")) {
//            int ss = text.indexOf("@");
//            String last = text.substring(ss, text.length());
//            if (last.contains("@")) {
//                Toast toast = Toast.makeText(getActivity(), "抄送邮箱多于一个", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                return;
//            }
//        }
//    }

    private void toast(String type) {
        Toast toast = Toast.makeText(getActivity(), type, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void refreshView() {
        Email email = ErmuBusiness.getCamSettingBusiness().getCamEmail(deviceId);
        if (email == null) {
            return;
        }
        mPassword.setText(email.getPasswd());
        mName.setText(email.getUser());
        mSender.setText(email.getFrom());
        mChaosong.setText(email.getCc());
        mPort.setText(email.getPort());
        mServer.setText(email.getServer());
        mReceiver.setText(email.getTo());
    }

    public static boolean isEmailAddress(String email) {
        Pattern p = Pattern.compile(regEx_email);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public void onSetDevEmail(Business bus) {
//        boolean success = (bus.getCode()== BusinessCode.SUCCESS);
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                break;
            case BusinessCode.SUCCESS:
//                ErmuApplication.toast("设置成功");
                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                break;
        }
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business business) {
        if(dialog !=null)  dialog.dismiss();
        if (!business.isSuccess()) {
            ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+business.getErrorCode()+")");
        }
    }
}

