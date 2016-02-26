package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iermu.R;

/**
 * 接受其他用用户的邀请
 * Created by zhoushaopei on 15/8/14.
 */
public class ConnCamDialog extends Dialog{

    private Context context;
    private ClickListenerInterface clickListenerInterface;
    private Button checkNow;
    private EditText camName;
    private String mName;

    public interface ClickListenerInterface {
        public void doCheck(String name);
    }

    public ConnCamDialog(Context context, String name) {
        super(context, R.style.load_dialog);
        this.context = context;
        this.mName = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.update_dev_name, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);

        checkNow = (Button) view.findViewById(R.id.btn_see_dev);
        camName = (EditText) view.findViewById(R.id.dev_name);

        checkNow.setOnClickListener(new clickListener());
        if (!TextUtils.isEmpty(mName)) camName.setText(mName);

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
            case R.id.btn_see_dev:
                String name = camName.getText().toString();
                clickListenerInterface.doCheck(name);
                break;
            }
        }
    }
}
