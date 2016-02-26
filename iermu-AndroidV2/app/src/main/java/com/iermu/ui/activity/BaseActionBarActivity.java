package com.iermu.ui.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.baidu.mobstat.StatService;
import com.iermu.ui.fragment.BaseFragment;

/**
 * Created by wcy on 15/6/18.
 */
public class BaseActionBarActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseFragment.popBackAllStack(this);
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setShowHideAnimationEnabled(false);
        }
        FragmentManager manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState); //解决Activity被回收问题
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState); //解决Activity被回收问题
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentManager manager = getSupportFragmentManager();
        manager.removeOnBackStackChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean b = BaseFragment.onKeyDown(this, keyCode, event);
        return b ? b : super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackStackChanged() {
        BaseFragment.onBackStackChanged(this);
    }
}
