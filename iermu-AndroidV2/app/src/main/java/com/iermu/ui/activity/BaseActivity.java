package com.iermu.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.baidu.mobstat.StatService;

/**
 * Created by wcy on 15/10/18.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStackHelper.addActivityStack(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
