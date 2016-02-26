package com.iermu.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.baidu.android.glview.GL2RenderJNIView;
import com.baidu.lightapp.plugin.videoplayer.coreplayer.LivePlayerControl;
import com.baidu.lightapp.plugin.videoplayer.coreplayer.LivePlayerControllerListener;
import com.github.pedrovgs.DraggableView;
import com.iermu.R;
import com.iermu.client.util.Logger;
import com.viewinject.ViewHelper;

/**
 * 公共摄像头直播页面
 *
 * Created by wcy on 15/6/19.
 */
public class PubCamLiveFragment extends BaseFragment implements LivePlayerControllerListener {

//    @ViewInject(R.id.draggable_play_view)
    DraggableView mPlayView;
//    @ViewInject(R.id.pubcam_live_holder)
    LinearLayout mPubCamView;

//    GL2RenderJNIView mGL2RenderView;
//    LivePlayerControl mPlayerCtrl;

    static {
        try {
            System.loadLibrary("glrender");
            System.loadLibrary("ffmpeg");
            System.loadLibrary("liveplayer");
            System.loadLibrary("audioels");
        } catch (UnsatisfiedLinkError e) {
            Logger.e("load library failed", e);
        }
    }

    @Override
    public void onCreateActionBar(BaseFragment fragment) {}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewHelper.inject(getActivity());

//        mPlayerCtrl = new LivePlayerControl();
//        mPlayerCtrl.setControllerListener(this);
//        mPlayerCtrl.setLogLevel(Constants.LOGLEVEL.LOG_LEVEL_E);
    }

    //TODO 测试
    public void onClick(View view) {
//        mPlayView = (DraggableView) getActivity().findViewById(R.id.draggable_play_view);
//        mPlayView.setVisibility(View.VISIBLE);
//        mPlayView.maximize();
//        mPubCamView = (LinearLayout) getActivity().findViewById(R.id.pubcam_live_holder);
//        mGL2RenderView = new GL2RenderJNIView(getActivity());
//        mPubCamView.addView(mGL2RenderView);
//        mPlayerCtrl.setRender(mGL2RenderView.GetRenderHandle());

//        mPlayerCtrl.setOption("clearscreen", "0");
//        mPlayerCtrl.setVideoPath(mVideoSource);
//        mPlayerCtrl.start();
    }

    //@OnItemClick(R.id.)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        mPlayView = (DraggableView) getActivity().findViewById(R.id.draggable_play_view);
//        mPlayView.setVisibility(View.VISIBLE);
//        mPlayView.maximize();
//        //new GL2RenderJNIView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mPlayView!=null && mPlayView.isMaximized()) {
            mPlayView.minimize();
            mPlayView.setVisibility(View.GONE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPlayStatusChanged(LivePlayerControl.PLAYER_STATUS player_status, int i, int i2) {

    }

    @Override
    public void onCacheStatusChanged(LivePlayerControl.CACHE_STATUS cache_status) {

    }

    @Override
    public void onCachingUpdate(int i) {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onDurationUpdate(int i) {

    }

    @Override
    public void onPositionUpdate(int i) {

    }

    @Override
    public void onDebugInfoUpdate(String s) {

    }

    @Override
    public void onWarning(int i) {

    }

    @Override
    public void onHardDecodeFailed(int i) {

    }

    @Override
    public void onReadedBytes(int i) {

    }
}
