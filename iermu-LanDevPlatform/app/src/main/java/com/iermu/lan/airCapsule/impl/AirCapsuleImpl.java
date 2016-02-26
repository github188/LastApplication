package com.iermu.lan.airCapsule.impl;

import android.content.Context;
import android.util.Log;

import com.cms.iermu.cms.CmsCmdStruct;
import com.cms.iermu.cms.CmsDev;
import com.cms.iermu.cms.CmsErr;
import com.cms.iermu.cms.CmsNetUtil;
import com.cms.iermu.cms.CmsProtocolDef;
import com.cms.iermu.cms.upnp.UpnpUtil;
import com.iermu.lan.AirCapsuleApi;
import com.iermu.lan.LanDevPlatformApi;
import com.iermu.lan.model.AirCapsuleResult;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.model.Result;
import com.iermu.lan.utils.LanUtil;


/**
 * 空气胶囊
 * Created by zsj on 15/11/09.
 */
public class AirCapsuleImpl implements AirCapsuleApi {

//    static {
//        try {
//            System.loadLibrary("cmsNative");
//        } catch (UnsatisfiedLinkError e) {
//            Log.e("load library failed", e.toString());
//        }
//    }

    /**
     * 获取 空气胶囊数据
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @return AirCapsuleResult.temperature 温度
     * @return AirCapsuleResult.humidity 湿度
     */
    @Override
    public AirCapsuleResult getAirCapsuleData(Context context, String devId, String uid){
        LanUtil util  = new LanUtil();
        AirCapsuleResult result= new AirCapsuleResult(ErrorCode.SUCCESS);
        CmsCmdStruct cmd = util.getAirCapsuleStruct();
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            CmsCmdStruct cmsGet =  netUtil.getDevParam(context, cmd, new CmsErr(-1, ""));
            if(cmsGet==null||cmsGet.bParams.length==0){
                result.setErrorCode(ErrorCode.EXECUTEFAILED);
                return result;
            }
            double tem = (double)util.decodeShort(cmsGet.bParams, 0)/100.00;
            double hum = (double)util.decodeShort(cmsGet.bParams,2)/100.00;
            if(tem==0&&hum==0){
                result.setIsSport(false);
            }
            result.setHumidity(hum);
            result.setTemperature(tem);
        }
        return result;
    }



}
