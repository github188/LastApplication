package com.iermu.ui.fragment.setupdev;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.model.CamDev;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zsj on 16/1/26.
 */
public class FindNoDevFragment extends BaseFragment {
    @ViewInject(R.id.actionbar_title)
    TextView mActionBarTitle;

    public static Fragment actionInstance() {
        return new FindNoDevFragment();
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_add_camera);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        mActionBarTitle.setText(getResources().getString(R.string.prepare_conn_dev));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_devices, container, false);
        ViewHelper.inject(this, view);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮
        return view;

    }

    @OnClick(value = {R.id.cancel, R.id.add_type_change, R.id.actionbar_back})
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
            case R.id.add_type_change:
                Fragment fragment;
                int times = ErmuBusiness.getPreferenceBusiness().getDevAddErrorTimes()+1;
                ErmuBusiness.getPreferenceBusiness().setDevAddErrorTimes(times);
                if (times%2==1){
                    fragment = ResetDevFragment.actionInstance();
                    addToBackStack(fragment);
                }else{
                    super.popBackStack(SearchGuideDevFragment.class);
                }
                break;

        }
    }

}
