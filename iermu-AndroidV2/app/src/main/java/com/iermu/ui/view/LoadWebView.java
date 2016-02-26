package com.iermu.ui.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.iermu.R;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

/**
 * Created by xjy on 15/8/31.
 */
public class LoadWebView extends RelativeLayout {

    @ViewInject(R.id.add_dev_progress)
    ProgressBar mProgress;
    private Context context;
    private int TIME = 500;
    private int num = 1;
    Handler handler = new Handler();

    public LoadWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.load_web, this, true);
        ViewHelper.inject(this, view);
        handler.postDelayed(runnable, TIME);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int times = num++;
            handler.postDelayed(this, TIME);
            mProgress.setProgress(times);
        }
    };

}
