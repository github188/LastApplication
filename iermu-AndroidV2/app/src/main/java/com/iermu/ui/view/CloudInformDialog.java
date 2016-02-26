package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.ui.activity.WebActivity;

/**
 * Created by xjy on 15/8/15.
 */
public class CloudInformDialog extends Dialog implements View.OnClickListener {
//    @ViewInject(R.id.no_inform) Button mNoInform;
//    @ViewInject(R.id.btn_buy) Button mBtnBuy;
//    //TODO 到期时间
//    @ViewInject(R.id.cloud_time) TextView mCloudTime;
//    //TODO 摄像机名称
//    @ViewInject(R.id.dev_name) TextView mDevName;
//    @ViewInject(R.id.cloud_timer) TextView mTimer;
    TextView mTimer;

    public CloudInformDialog(Context context, int theme) {
        super(context,R.style.custom_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.custom_cloud_inform_);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setCanceledOnTouchOutside(true);
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        View view = inflater.inflate(R.layout.custom_cloud_inform_, null);
        setContentView(R.layout.custom_cloud_inform_);
        mTimer = (TextView) findViewById(R.id.cloud_timer);
        Button btnOk = (Button) findViewById(R.id.no_inform);
        Button btnBuy = (Button) findViewById(R.id.btn_buy);
        btnOk.setOnClickListener(this);
        btnBuy.setOnClickListener(this);

//        Window dialogWindow = getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        DisplayMetrics d = getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
//        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
//        lp.width = d.widthPixels;
//        lp.height = d.heightPixels;
//        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.no_inform:
                CloudInformDialog.this.dismiss();
                break;
            case R.id.btn_buy:
                WebActivity.actionStartWeb(getContext(),WebActivity.PAGE_CVRBUY);
                break;
        }
    }
    public void setStatusText(String text) {
        mTimer.setText(text);
    }


}
