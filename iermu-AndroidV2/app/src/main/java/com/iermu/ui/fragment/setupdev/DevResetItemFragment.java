package com.iermu.ui.fragment.setupdev;

import android.animation.StateListAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.ui.activity.MainActivity;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by zsj on 16/1/26.
 */
public class DevResetItemFragment extends Fragment {

    @ViewInject(R.id.reset_dev_item1)RelativeLayout resetDevLayout1;
    @ViewInject(R.id.reset_dev_item2)RelativeLayout resetDevLayout2;
    @ViewInject(R.id.textview_tip1)TextView textViewTip1;
    @ViewInject(R.id.textview_tip2)TextView textViewTip2;
    @ViewInject(R.id.reset_dev_bottom)ImageView reset_dev_bottom;
    @ViewInject(R.id.reset_dev_hole)ImageView reset_dev_hole;
    @ViewInject(R.id.reset_dev_needle)ImageView reset_dev_needle;
    @ViewInject(R.id.reset_dev_ball)ImageView reset_dev_ball;
    @ViewInject(R.id.reset_dev_hand_normal)ImageView reset_dev_hand_normal;
    @ViewInject(R.id.reset_dev_hand_press)ImageView reset_dev_hand_press;

    private final static String NUMBER = "number";

    AnimatorSet aSet1;
    AnimatorSet aSet2;
    TranslateAnimation t_animation;
    int num = 0;

    public static Fragment actionInstance(int number) {
        DevResetItemFragment devResetItemFragment = new DevResetItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(NUMBER, number);
        devResetItemFragment.setArguments(bundle);
        return devResetItemFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_dev_item, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        Bundle bundle = getArguments();
        int number = bundle.getInt(NUMBER);
        if(number==1){
            num = 1;
            resetDevLayout1.setVisibility(View.VISIBLE);
            SpannableStringBuilder ssb = new SpannableStringBuilder(getString(R.string.reset_cam_tip1));
            ssb.setSpan(new AbsoluteSizeSpan(24,true), 10, 12,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(0xffffa032), 10, 12,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewTip1.setText(ssb);

            aSet1 = new AnimatorSet();
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(reset_dev_bottom, "translationX", Util.DensityUtil.dip2px(getActivity(),-33));
            animator1.setDuration(1000);

            ObjectAnimator animator2 = ObjectAnimator.ofFloat(reset_dev_hole, "translationX", Util.DensityUtil.dip2px(getActivity(),-33));
            animator2.setDuration(1000);

            ObjectAnimator animator3 = ObjectAnimator.ofFloat(reset_dev_needle,"alpha", 0f, 1f);
            animator3.setDuration(500);

            ObjectAnimator animator4 = ObjectAnimator.ofFloat(reset_dev_needle,"translationX", Util.DensityUtil.dip2px(getActivity(),-14));
            animator4.setDuration(1000);

            ObjectAnimator animator5 = ObjectAnimator.ofFloat(reset_dev_needle,"translationY", Util.DensityUtil.dip2px(getActivity(),-11));
            animator5.setDuration(1000);

            aSet1.play(animator1);
            aSet1.play(animator2).with(animator1);
            aSet1.play(animator3).after(animator1);
            aSet1.play(animator4).after(animator3);
            aSet1.play(animator5).after(animator3);

        }else{
            num = 2;
            resetDevLayout2.setVisibility(View.VISIBLE);
            SpannableStringBuilder ssb = new SpannableStringBuilder(getString(R.string.reset_cam_tip2));
            ssb.setSpan(new AbsoluteSizeSpan(24,true), 11, 13,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(0xffffa032), 11, 13,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewTip1.setText(ssb);

            aSet2 = new AnimatorSet();
            ObjectAnimator animator6 = ObjectAnimator.ofFloat(reset_dev_hand_normal, "translationX", Util.DensityUtil.dip2px(getActivity(),-61));
            animator6.setDuration(1500);
            ObjectAnimator animator7 = ObjectAnimator.ofFloat(reset_dev_hand_normal, "translationY", Util.DensityUtil.dip2px(getActivity(),-59));
            animator7.setDuration(1500);
            ObjectAnimator animator8 = ObjectAnimator.ofFloat(reset_dev_hand_normal,"alpha", 0f, 1f);
            animator8.setDuration(1500);
            aSet2.play(animator8);
            aSet2.play(animator6).with(animator8);
            aSet2.play(animator7).with(animator8);
            animator7.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    reset_dev_hand_normal.setAlpha(0.0f);
                    reset_dev_hand_press.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(getString(R.string.reset_cam_tip3));
        ssb.setSpan(new AbsoluteSizeSpan(24,true), 5, 9,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(0xffffa032), 5, 9,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewTip2.setText(ssb);
        return view;
    }

    @Override
    public void onResume() {
        if(num==1){
            reset_dev_needle.setAlpha(0.0f);
            reset_dev_needle.setTranslationX(0);
            reset_dev_needle.setTranslationY(0);
            reset_dev_bottom.setTranslationX(0);
            aSet1.start();
        }else{
            reset_dev_hand_normal.setTranslationX(0);
            reset_dev_hand_normal.setTranslationY(0);
            reset_dev_hand_press.setVisibility(View.GONE);
            reset_dev_hand_normal.setAlpha(0.0f);
            aSet2.start();
        }
        super.onResume();
    }
}
