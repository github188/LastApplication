package com.iermu.ui.fragment.setupdev;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.ViewPagerZoom.FixedScroller;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 重置摄像机
 *
 * Created by zsj on 16/1/26.
 */
public class ResetDevFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.view_pager)        ViewPager mViewPager;
    @ViewInject(R.id.viewpager_lay)     RelativeLayout mViewPagerLay;
    @ViewInject(R.id.indicator)         CirclePageIndicator indicator;
    @ViewInject(R.id.actionbar_title)   TextView title;

    private Field mScroller = null;
    private ViewPagerAdapter mAdapter;
    ArrayList<Fragment> fragments;

    Timer timer = new Timer();
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            if(mViewPager.getCurrentItem()==0)
                message.what = 1;
            else
                message.what = 0;
            handler.sendMessage(message);
            timer.cancel();
        }
    };

    final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.setCurrentItem(msg.what, true);
        }
    };

    /**
     * 启动摄像机添加向导页面
     * @return
     */
    public static Fragment actionInstance() {
        ResetDevFragment fragment = new ResetDevFragment();
        return fragment;
    }

    @Override
    public void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_add_camera);
        setCommonTitle(R.string.reset_dev);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_dev, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        initViewPager();

        //开始一个定时任务
        timer.schedule(mTimerTask, 4000, 40000);

        return view;
    }


    private void initViewPager() {

        mViewPager.setOnPageChangeListener(this);
        mAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);

        indicator.setViewPager(mViewPager);
        indicator.setOnPageChangeListener(this);

        mViewPagerLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });

        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
            FixedScroller scroller = new FixedScroller(mViewPager.getContext(), sInterpolator);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    @OnClick(value = {R.id.cancel, R.id.reset_dev, R.id.actionbar_back})
    public void onClick(View v) {
        ISetupDevBusiness devBusiness = ErmuBusiness.getSetupDevBusiness();
        switch (v.getId()) {
            case R.id.cancel:
                popBackAllStack();
                devBusiness.quitScanCam();
                break;
            case R.id.actionbar_back:
                popBackStack();
                break;
            case R.id.reset_dev:
                super.popBackStack(ConfigWifiFragment.class);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            popBackStack();
        }
        return true;
    }

    private void refreshView() {
        fragments = new ArrayList<Fragment>();
        fragments.add(DevResetItemFragment.actionInstance(1));
        fragments.add(DevResetItemFragment.actionInstance(2));
        mAdapter.setFragments(fragments);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int position) {
        Fragment f = fragments.get(position);
        f.onResume();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> fragments = new ArrayList<Fragment>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void setFragments(List<Fragment> frag) {
            this.fragments = frag;
            notifyDataSetChanged();
        }
    }
}
