package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.ui.util.Util;

/**
 * 固件升级提示
 * Created by zsj on 16/1/14.
 */
public class UpgradeTipDialog extends Dialog {

    private Context context;
    private ClickListenerInterface clickListenerInterface;

    private String version;
    private String[] tip;

    public interface ClickListenerInterface {
        public void doConfirm();

        public void doCancel();
    }

    public UpgradeTipDialog(Context context, String version, String[] tip) {
        super(context, R.style.load_dialog);
        this.context = context;
        this.version = version;
        this.tip = tip;
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_upgrade_tips_dialog);
        TextView titleTip = (TextView) findViewById(R.id.cam_upgrade_message_top);
        titleTip.setText(ErmuApplication.getContext().getString(R.string.update_tip1) + " " +version + " " + ErmuApplication.getContext().getString(R.string.update_tip2));
        final LinearLayout mLay = (LinearLayout) findViewById(R.id.cam_upgrade_message);


        for (int i = 0; i < tip.length; i++) {
            if ("".equals(tip[i]) || tip[i] == null) break;
            TextView message = new TextView(context);
            message.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            message.setTextSize(13);
            message.setLineSpacing(5,1);
            message.setTextColor(context.getResources().getColor(R.color.cam_upgrade_new));
            message.setPadding(Util.DensityUtil.dip2px(context,12), 0, Util.DensityUtil.dip2px(context,20), 0);

            SpannableStringBuilder ssb = new SpannableStringBuilder(tip[i]);
            ssb.setSpan(new BulletSpan(Util.DensityUtil.dip2px(context,4), 0xff00acef), 0, tip[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            message.setText(ssb);
            mLay.addView(message);
        }

        TextView cancle = (TextView) findViewById(R.id.cam_upgrade_cancle);

        TextView sure = (TextView) findViewById(R.id.cam_upgrade_sure);
        ImageButton mAuthClose = (ImageButton) findViewById(R.id.buttonClose);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.cam_upgrade_message_scrollView);

        cancle.post(new Runnable() {
            @Override
            public void run() {
                int w = mLay.getHeight();
                if(w>Util.DensityUtil.dip2px(context,120)){
                    scrollView.getLayoutParams().height=Util.DensityUtil.dip2px(context,120);
                }
            }
        });



        cancle.setOnClickListener(new clickListener());
        sure.setOnClickListener(new clickListener());
        mAuthClose.setOnClickListener(new clickListener());
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cam_upgrade_sure:
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.cam_upgrade_cancle:
                    clickListenerInterface.doCancel();
                    break;
                case R.id.buttonClose:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }
}
