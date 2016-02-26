package com.iermu.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iermu.R;

/**
 * Created by xjy on 15/11/20.
 */
public class SettingButtonLoadView extends RelativeLayout {

    private ImageView loadOut;
    Animation buttonLoad;
    AnimationDrawable animationDrawable;

    public SettingButtonLoadView(Context context) {
        super(context);
    }

    public SettingButtonLoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_buttom_loading, this, true);
        loadOut = (ImageView) findViewById(R.id.load_out);
        animationDrawable = (AnimationDrawable) loadOut.getDrawable();
    }

    public void startAnimation() {
       animationDrawable.start();
    }

    public void stopAnimation() {
        animationDrawable.stop();
    }

    public void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public void setInvisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }
}
