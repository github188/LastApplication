package com.iermu.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iermu.R;

/**
 * Created by xjy on 15/7/8.
 */
public class LoadingView extends RelativeLayout {
    private ImageView loadIn;
    private ImageView loadOut;
    Animation animationLoad;
    Animation  animation;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_load, this, true);
        loadIn = (ImageView) findViewById(R.id.load_in);
        loadOut = (ImageView) findViewById(R.id.load_out);
        animationLoad = AnimationUtils.loadAnimation(context, R.anim.rotate_load);
        animation = AnimationUtils.loadAnimation(context, R.anim.rotate_load_out);
    }

    public void startAnimation(){
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animationLoad.setInterpolator(lin);
        loadIn.startAnimation(animationLoad);
        loadOut.startAnimation(animation);
    }

    public void stopAnimation() {
        if(animation!=null && animationLoad!=null){
            loadIn.clearAnimation();
            loadOut.clearAnimation();
        }

    }
}
