package com.iermu.client.business.impl.setupdev;

import android.os.SystemClock;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.business.api.PubCamApi;
import com.iermu.client.business.api.response.CamMetaResponse;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamLive;
import com.iermu.client.util.Logger;

/**
 * //经测试发现规律: Ap|Smart最长超时是需要超过60秒的(建议采用80-100)
 *
 * Created by zhoushaopei on 15/10/23.
 */
public class CheckDevOnLineRunnable extends Thread {

    private static final int MIN_TIMEOUT    = 10*1000;
    private static final int MAX_TIMEOUT    = 80*1000;
    private String accessToken;
    private String deviceId;
    private DevOnLineListener listener;
    private CamDev dev;             //连接的设备
    private long startTime;
    private boolean interrupted = false;
    CamLive camLive = null;

    public CheckDevOnLineRunnable(CamDev dev) {
        this.dev = dev;
        this.accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.interrupted = true;
    }

    @Override
    public void run() {
        deviceId = dev.getDevID();
        Logger.i("CheckDevOnLineRunnable", "checkoutDevOnLine start deviceId:" + deviceId);
        startTime = System.currentTimeMillis();
        checkDevOnline();

        if(interrupted) return;
        if(isOnline(camLive)) {
            notifyDevOnline(camLive);
        } else {
            notifyTimeout();
        }
    }

    private void checkDevOnline() {
        while (!isOnline(camLive)
                && (System.currentTimeMillis()-startTime)<MAX_TIMEOUT
                && !interrupted ) { //设备没有上线 && 没有超时
            if (ErmuApplication.isConnected()) {
                CamMetaResponse res = PubCamApi.apiCamMeta(deviceId, accessToken, "", "");
                camLive = res.getCamLive();
                int status = (camLive!=null) ? camLive.getStatus():0;
                //羚羊 (camLive!=null) && camLive.getConnectType() == ConnectType.LINYANG
                Logger.i("CheckDevOnLineRunnable", "deviceId:"+deviceId+" camLive:"+camLive+" status:"+status);
            }
            SystemClock.sleep(100);
            Logger.i("CheckDevOnLineRunnable", "scan num:"+deviceId);
        }
    }

    private void notifyDevOnline(CamLive live) {
        if (listener != null) {
            listener.onDevOnLine(live);
        }
    }
    private void notifyTimeout() {
        if (listener != null) {
            listener.onTimeout();
        }
    }

    public void setOnDevOnLine(DevOnLineListener listener) {
        this.listener = listener;
    }

    //判断设备是否上线
    private boolean isOnline(CamLive live) {
        return (live!=null) && (live.getStatus()>0);
    }

    public interface DevOnLineListener{

        void onDevOnLine(CamLive camLive);

        void onTimeout();
    }

}
