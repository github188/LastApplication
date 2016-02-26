package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
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
 * Created by xjy on 15/11/26.
 */
public class PersonChooseImgDialog extends Dialog implements View.OnClickListener {

    chooseHeadImg listener;



        public PersonChooseImgDialog(Context context, int theme) {
        super(context, R.style.load_dialog);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();

    }
    public void setOnChooseListener(chooseHeadImg listener) {
        this.listener = listener;
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = inflater.inflate(R.layout.choose_head, null);
        setContentView(view, params);
        TextView head = (TextView) findViewById(R.id.choose_img);
        TextView cancelImg = (TextView) findViewById(R.id.cancel);
        head.setOnClickListener(this);
        cancelImg.setOnClickListener(this);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getContext().getResources().getDisplayMetrics();
        lp.width = d.widthPixels;
        lp.height = d.heightPixels;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose_img:
                if (listener != null){
                    listener.callBack();
                }
                break;
            case R.id.cancel:
                PersonChooseImgDialog.this.dismiss();
                break;
        }
    }
    public  interface chooseHeadImg{
        void callBack();
    }
}
