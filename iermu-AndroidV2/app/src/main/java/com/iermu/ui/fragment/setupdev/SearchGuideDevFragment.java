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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.util.LanguageUtil;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xjy on 15/10/12.
 */
public class SearchGuideDevFragment extends BaseFragment {

    @ViewInject(R.id.actionbar_title)
    TextView mActionBarTitle;
    @ViewInject(R.id.no_blue_or_red)
    TextView mNoBlueOrRed;
    @ViewInject(R.id.start_configurtion)
    Button mStartConfiguition;
    @ViewInject(R.id.blue_light_img_)
    ImageView mBlueLight;
    @ViewInject(R.id.actionbar_back)
    ImageView mActionBack;
    private AlertDialog dialog;
    private boolean isRed;
    private Timer timer;

    private Handler m_handler;
    private Fragment fragment;
    private boolean isHiwifi;
    MediaPlayer mediaPlayer;
    private int current;
    AudioManager mAudioManager;

    public static Fragment actionInstance(Context context) {
        return new SearchGuideDevFragment();
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_about);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        mActionBarTitle.setText(getResources().getString(R.string.prepare_conn_dev));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_guide, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮
        mNoBlueOrRed.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        timer = new Timer();
        setTimerTask();
        mediaPlayer = MediaPlayer.create(getActivity(),R.raw.reb_blue_light);
        boolean isFirst = ErmuBusiness.getPreferenceBusiness().getRebBlueLight();
        String language = LanguageUtil.getLanguage();
        if (isFirst){
            mAudioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
            current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int count  = (max-1)/2+1;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, count, 0);
            if (language.equals("zh")) mediaPlayer.start();
            isFirst = false;
            ErmuBusiness.getPreferenceBusiness().setRebBlueLight(isFirst);
        }
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        mediaPlayer.stop();
        ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
        ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
    }

    private void setTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, 100, 700);
    }

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    if (isRed) {
                        mBlueLight.setImageResource(R.drawable.red_light_dev);
                    } else {
                        mBlueLight.setImageResource(R.drawable.blue_light_dev);
                    }
                    isRed = !isRed;
                    break;
            }
        }
    };

    @OnClick(value = {R.id.actionbar_back, R.id.start_configurtion, R.id.no_blue_or_red})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_back:
                popBackStack();
                break;
            case R.id.start_configurtion:
//                m_handler = new Handler() {
//                    public void handleMessage(Message msg) {
//
////                Fragment fragment = ScanDevFragment.actionInstance(getActivity());
////                Fragment fragment = new ConfigWifiFragment().actionInstance(getActivity());
//                        if(msg.what==1){
//                            fragment = ScanDevFragment.actionInstance(getActivity(), new CamDevConf(""));
//                        }else{
//                            fragment = new ConfigWifiFragment().actionInstance(getActivity());
//                        }
//                        BaseFragment.addToBackStack(getActivity(), fragment);
//
//                    }
//                };
//                new Thread(new Runnable() {
//                    public void run() {
//                        isHiwifi = HiWifiApi.isHiwifi();
//                        Message msg = new Message();
//                        msg.what = isHiwifi?1:0;
//                        m_handler.sendMessage(msg);
//                    }
//                }).start();
                mediaPlayer.stop();
                ((AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
                Fragment fragment = new ConfigWifiFragment().actionInstance(getActivity());
                super.addToBackStack(fragment);
                break;
            case R.id.no_blue_or_red:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View view = View.inflate(getActivity(), R.layout.conn_device_help, null);
                    Button mKnowledge = (Button) view.findViewById(R.id.knowledge_btn);
                    TextView mOfficalPhone = (TextView) view.findViewById(R.id.offical_phone);
                    TextView mTitleText = (TextView) view.findViewById(R.id.title_text);
                    mTitleText.setText(getResources().getString(R.string.red_blue_text));
                    mOfficalPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    mOfficalPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: + 400-898-8800"));
                            getActivity().startActivity(intent);
                        }
                    });
                    mKnowledge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    builder.setView(view);
                    dialog = builder.create();
                    dialog.show();
                break;
        }
    }

}
