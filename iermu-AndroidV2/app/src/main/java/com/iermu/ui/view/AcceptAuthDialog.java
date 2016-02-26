package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iermu.R;

/**
 * 接受其他用用户的邀请
 * Created by zhoushaopei on 15/8/14.
 */
public class AcceptAuthDialog extends Dialog{

    private Context context;
    private ClickListenerInterface clickListenerInterface;

    private String deviceName;
    private String uuName;

    public interface ClickListenerInterface {
        public void doConfirm();
        public void doCancel();
    }

    public AcceptAuthDialog(Context context, String name, String deviName) {
        super(context,R.style.load_dialog);
        this.context = context;
        this.deviceName = deviName;
        this.uuName = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.accept_auth, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        TextView mUserName = (TextView) view.findViewById(R.id.user_name);
        TextView mDevName = (TextView) view.findViewById(R.id.dev_name);
        ImageButton mAuthClose = (ImageButton) view.findViewById(R.id.auth_close);
        Button mAuthOk = (Button) view.findViewById(R.id.auth_ok);

        mDevName.setText(deviceName);
        mUserName.setText(uuName);

        mAuthOk.setOnClickListener(new clickListener());
        mAuthClose.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        //lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        lp.width = d.widthPixels;
        lp.height = (int) (d.heightPixels * 0.8);
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.auth_ok:
                clickListenerInterface.doConfirm();
                break;
            case R.id.auth_close:
                clickListenerInterface.doCancel();
                break;
            }
        }
    }
}
