package com.iermu.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zsj on 15/1/18.
 */
public class UpgradeViewloading extends RelativeLayout {

    private ImageView loading;
    AnimationDrawable animationDrawable;
    TextView textTip ;
    TextView textProgress ;
    TextView bfh ;
    public UpgradeViewloading(Context context) {
        super(context);
    }

    public UpgradeViewloading(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.upgrade_loading, this, true);
        loading = (ImageView) findViewById(R.id.upgrade_lodding_img);
        loading.setImageResource(R.drawable.upgrade_loading);
        textTip = (TextView) findViewById(R.id.upgrade_text);
        textProgress = (TextView) findViewById(R.id.upgrade_progress);
        bfh = (TextView) findViewById(R.id.upgrade_bfh);

    }
    public void  startAnimation(){
        bfh.setVisibility(View.VISIBLE);
        textProgress.setVisibility(View.VISIBLE);
        animationDrawable = (AnimationDrawable) loading.getDrawable();
        animationDrawable.start();
    }
    public void stopAnimation(){
        animationDrawable.stop();
    }
    public void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }
    public void setInvisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }
    public void setTextTip(String tip){
        textTip.setText(tip);
    }
    public void setTextProgress(int progress){
            textProgress.setText(progress+"");
    }
    public void setLoadingImg(int status){

        if(status==-1||status==-2){
            loading.setImageResource(R.drawable.upgrade_load_img_fail);
            bfh.setVisibility(View.INVISIBLE);
            textProgress.setVisibility(View.INVISIBLE);
        }
        else if (status==5){
            textProgress.setText(100+"");
            loading.setImageResource(R.drawable.upgrade_load_img_success);
        }
    }

}
