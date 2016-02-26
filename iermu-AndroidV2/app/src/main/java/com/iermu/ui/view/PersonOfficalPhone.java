package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.iermu.R;


/**
 * Created by xjy on 15/8/4.
 */
public class PersonOfficalPhone extends Dialog implements View.OnClickListener {


    protected PersonOfficalPhone(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public PersonOfficalPhone(Context context, int theme) {
        super(context, R.style.load_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();

    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = inflater.inflate(R.layout.custom_dialog, null);
        setContentView(view, params);
        TextView phone = (TextView) findViewById(R.id.offical_phone);
        TextView cancelPhone = (TextView) findViewById(R.id.offical_cancel);
        phone.setOnClickListener(this);
        cancelPhone.setOnClickListener(this);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getContext().getResources().getDisplayMetrics();
        lp.width = d.widthPixels;
        lp.height = d.heightPixels;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.offical_phone:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "400-898-8800"));
                getContext().startActivity(intent);
                break;
            case R.id.offical_cancel:
                PersonOfficalPhone.this.dismiss();
                break;
        }
    }

}
