package com.iermu.ui.util;

import com.cms.media.record.cmsAudioRecorder;
import com.iermu.bdchannel;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.util.Logger;
import com.lingyang.sdk.impl.IErmuSDK;
import com.lyy.lyyapi;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangxq on 15/9/24.
 */
public class CmsTalk {
    private cmsAudioRecorder audioRecorder;
    private lyyapi api;
    private CallBack listener;
    private bdchannel bd;

    private boolean isFirst = true; // 判断是否是第一次连接
    private boolean isConnected; // 百度语音是否连接成功
    private boolean isClose = true; // 判断通道是否关闭

    private boolean isStop;    // 标志手是否一已经放开
    private boolean isLLY; // 是否是羚羊云设备

    private Timer timer;
    private Timer timerRecording;

    public CmsTalk() {
        api = IErmuSDK.getInstance().getLYYApi();
    }

    /**
     * 语音对讲连接
     *
     * @param deviceId
     * @param accessToken
     * @param streamId
     * @return
     */
    public void connectAudio(final String deviceId, final String accessToken, final String streamId) {
        Logger.d(">>>>>>>打开通道");
        isStop = false;
        if (!isFirst && !(isConnected && isClose)) {
            Logger.d(">>>>>>>通道未打开");
            return;
        }
        bd = null;
        isFirst = false;
        isConnected = false;
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                bd = new bdchannel();
                bd.SetOption("devid", deviceId);
                bd.SetOption("accesstoken", accessToken);
                bd.SetOption("user", "true");
                bd.SetOption("devtype", "2");
                String baiduAccessToken = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
                //String rtmpServer = StreamMediaApi.getAudioCHRtmpServer(baiduAccessToken, deviceId, streamId);
                //rtmpServer += "/"+ streamId+"?deviceid=" + deviceId;
                //"rtmp://qd.cam.baidu.com:1935/live/866879a040d711e5bbdff80f41fbe890?deviceid=" + deviceId
                int ret = bd.Connect("rtmp://qd.cam.baidu.com:1935/live/866879a040d711e5bbdff80f41fbe890?deviceid=" + deviceId);
                Logger.d("retret:" + ret);
                isConnected = true;
                isClose = false;
                if (ret == 0) {
                    Logger.d(">>>>>>>通道已打开");
                    bd.Start();
                    if(audioRecorder==null) audioRecorder = new cmsAudioRecorder(bd);
                    startAudio();
                } else {
                    Logger.d(">>>>>>>通道打开失败");
                    if (listener != null) {
                        bd.Close();
                        isClose = true;
                        listener.onConnectClosed();
                        if(timerRecording!=null) timerRecording.cancel();
                    }
                }
            }
        });
    }

    /**
     * 初始化羚羊云对讲对象
     *
     * @param deviceId
     */
    public void initLLYAudio(String deviceId) {
        isLLY = true;
        if(audioRecorder==null) audioRecorder = new cmsAudioRecorder(api);
    }

    /**
     * 开始语音
     */
    public void startAudio() {
        if (isLLY) {
            Logger.d("llyStart");
            if(audioRecorder!=null) audioRecorder.startRecord();
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new StrengthTimer(), 0, 100);
        } else {
            if (isStop) {
                if (!isClose) {
                    bd.Close();
                    isClose = true;
                    Logger.d(">>>>>>>通道已关闭");
                }
            } else {
                Logger.d("baiduStart");
                if (audioRecorder == null) audioRecorder = new cmsAudioRecorder(bd);
                audioRecorder.startRecord();
                Logger.d(">>>>>>>打开录制");
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule(new StrengthTimer(), 0, 100);
            }
        }
    }

    /**
     * 结束语音
     */
    public void stopAudio() {
        isStop = true;
        if (audioRecorder != null) {
            audioRecorder.stopRecord();
            timerRecording = new Timer();
            timerRecording.schedule(new RecordingTimer(), 0, 20);
            Logger.d(">>>>>>>关闭录制");
        } else {
            if (listener != null) {
                listener.onConnectClosed();
                if(timerRecording!=null) timerRecording.cancel();
            }
            if (isConnected && bd != null) {
                if (!isClose) {
                    bd.Close();
                    isClose = true;
                    Logger.d(">>>>>>>通道已关闭");
                }
            }
        }
        if (timer != null) {
            timer.cancel();
        }
        if (listener != null) {
            listener.onStrengthChanged(0);
        }
    }

    /**
     * 释放录音资源
     */
    public void releaseAudioRecorder() {
        if (audioRecorder != null) {
            audioRecorder.stopRecord();
            audioRecorder.release();
        }
        if (timer != null) {
            timer.cancel();
        }
        if (timerRecording != null) {
            timerRecording.cancel();
        }
    }

    public void setListener(CallBack callBack) {
        this.listener = callBack;
    }

    public interface CallBack {

        /**
         * 音量变化时调用
         */
        void onStrengthChanged(int strength);

        /**
         * 通道关闭后调用
         */
        void onConnectClosed();
    }

    private class StrengthTimer extends TimerTask {

        @Override
        public void run() {
            if (listener != null) {
                int audioStrength = (int) audioRecorder.getAudioStrength();
                Logger.d("audioStrength:" + audioStrength);
                listener.onStrengthChanged(audioStrength);
            }
        }
    }

    private class RecordingTimer extends TimerTask {

        @Override
        public void run() {
            Logger.d("recording...");
            if (audioRecorder != null && !audioRecorder.isRecording()) {
                Logger.d(">>>>>>>录制已关闭");
                if (listener != null) {
                    listener.onConnectClosed();
                    timerRecording.cancel();
                }

                if (isConnected && bd != null) {
                    if (!isClose) {
                        bd.Close();
                        isClose = true;
                        Logger.d(">>>>>>>通道已关闭");
                    }
                }
            }
        }
    }
}
