package com.iermu.client.business.impl.setupdev.setup;

import android.text.TextUtils;

import com.iermu.client.ErmuBusiness;
import com.iermu.client.business.api.CamDeviceApi;
import com.iermu.client.business.api.PubCamApi;
import com.iermu.client.business.api.response.CamMetaResponse;
import com.iermu.client.business.api.response.RegisterDevResponse;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.LiveDataType;

/**
 * Created by zsj on 15/12/4.
 * 设备注册
 * 注册成功:
 *  1. 设备未注册, 然后注册设备成功
 *  2. 设备已注册, 但设备是自己的, 所以算注册成功
 *
 * 注册失败:
 *  1. 设备未注册, 调用注册接口失败
 *  2. 设备已注册, 但设备是别人的, 所以注册失败(NO_PERMISSION)
 *  3. 设备已注册, 但调用查询Meta接口失败
 *
 */
public class RegisterDevRunnable extends Thread{

    private RegisterDevListener listener;
    private String deviceId;
    private int deviceType;
    private int connectType;
    private String desc;
    private String accessToken;

    public RegisterDevRunnable(String deviceId, int connectType, String accessToken) {
        this.deviceId = deviceId;
        this.deviceType = 1;
        this.connectType = connectType;
        this.accessToken = accessToken;
        String desc = ErmuBusiness.getMimeCamBusiness().getCamDescription(deviceId);
        if (TextUtils.isEmpty(desc)) desc = "我的摄像机";
        this.desc = desc;
    }

    public RegisterDevRunnable setRegisterDevListener(RegisterDevListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void interrupt() {
        this.listener = null;
        super.interrupt();
    }

    @Override
    public void run() {
        super.run();
        RegisterDevResponse res = CamDeviceApi.registerDevice(deviceId, deviceType, connectType, desc, accessToken);
        Business bus = res.getBusiness();
        int code     = bus.getCode();
        String connectId="";
        if(code == BusinessCode.SUCCESS) {
            if(res.getConnectType() == ConnectType.LINYANG){
                connectId = res.getConnectId();
            } else {
                connectId  = res.getStreamId();
            }
        } else if(code == BusinessCode.DEV_REGISTED ) {
            CamMetaResponse metaRes = PubCamApi.apiCamMeta(deviceId, accessToken, "", "");
            CamLive live            = metaRes.getCamLive();
            if(live != null && live.getDataType()== LiveDataType.MIME) {
                if(live.getConnectType() == ConnectType.LINYANG){
                    connectId = (live!=null) ? live.getConnectCid() : "";
                } else {
                    connectId = (live!=null) ? live.getStreamId() : "";
                }
            } else {
                bus = metaRes.getBusiness();
                bus.setCode(BusinessCode.NO_PERMISSION);
            }
        }

        if(listener == null) return;
        listener.onRegisterDev(bus, connectId);
    }

    public interface RegisterDevListener {

        /**
         * 注册事件回调
         * @param bus       业务对象
         * @param connectId 平台ID
         */
        public void onRegisterDev(Business bus, String connectId);

    }
}
