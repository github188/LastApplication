package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;

/*
 * Created by xjy on 15/9/14.
 */
public class CloudStartCloseDialog extends Dialog implements View.OnClickListener{

    private TextView mText;
    private TextView mTimer;
    private ImageView mCloud;
    private ImageView recordClose;
//    setCloudDateListener listener;
    TextView serverTime;

    public CloudStartCloseDialog(Context context, int theme) {
        super(context, R.style.custom_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_cloud);
        mText = (TextView) findViewById(R.id.text);
        mTimer = (TextView) findViewById(R.id.end_timer);
        mCloud = (ImageView) findViewById(R.id.cloud_img);
        recordClose = (ImageView) findViewById(R.id.record_close);
        serverTime = (TextView) findViewById(R.id.server_time);
        recordClose.setOnClickListener(this);
        final Animation anima = AnimationUtils.loadAnimation(getContext(),R.anim.empty_view);
        mCloud.startAnimation(anima);
        anima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCloud.startAnimation(anima);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void setstatetext(String tex) {
        mTimer.setText(tex);
    }

    public void setstatetime(String str) {
        mText.setText(str);
    }

    public void setServerTime(int dayNum) {
        serverTime.setText(dayNum + "天云录像服务");
    }

    @Override
    public void onClick(View v) {
        CloudStartCloseDialog.this.dismiss();
    }



//    public interface  setCloudDateListener{
//        void  setCloudDate(String dateTime);
//    }
}
