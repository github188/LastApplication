package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.ui.activity.WebActivity;

/**
 * Created by xjy on 15/8/5.
 */
public class CloudOverTimeDialg extends Dialog implements View.OnClickListener {

    OnClickListener listener;

    protected CloudOverTimeDialg(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public CloudOverTimeDialg(Context context) {
        super(context, R.style.load_dialog);
    }

    public void setOnControlListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_customcloud);
        TextView btnOk = (TextView) findViewById(R.id.btn_ok);
        TextView btnBuy = (TextView) findViewById(R.id.btn_buy);
        btnOk.setOnClickListener(this);
        btnBuy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (listener != null) {
                    listener.onReturn(this);
                }
                break;
            case R.id.btn_buy:
                WebActivity.actionStartWeb(getContext(), WebActivity.PAGE_CVRBUY);
                break;
        }
    }

    public interface OnClickListener {
        void onReturn(CloudOverTimeDialg view);

        void onPopBack(CloudOverTimeDialg view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (listener != null) {
            listener.onPopBack(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
