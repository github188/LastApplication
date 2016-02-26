package com.iermu.lan.cardVidio.impl;

import android.content.Context;
import android.util.Log;

import com.cms.iermu.cms.CmsCmdStruct;
import com.cms.iermu.cms.CmsDev;
import com.cms.iermu.cms.CmsErr;
import com.cms.iermu.cms.CmsNetUtil;
import com.cms.iermu.cms.upnp.UpnpUtil;
import com.iermu.lan.AirCapsuleApi;
import com.iermu.lan.CardVidioApi;
import com.iermu.lan.model.AirCapsuleResult;
import com.iermu.lan.model.CardInforResult;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.utils.LanUtil;

import java.util.logging.Logger;


/**
 * 空气胶囊
 * Created by zsj on 15/11/11.
 */
public class CareVidioImpl implements CardVidioApi {

    /**
     * 获取 摄像机存储卡信息
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    @Override
    public CardInforResult getCardInfor(Context context, String devId, String uid){
        LanUtil util  = new LanUtil();
        CardInforResult result= new CardInforResult(ErrorCode.SUCCESS);
        CmsCmdStruct cmd = util.getCardInforStruct();
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        Log.d("ip:", ip==null?"null":ip);
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

            String st = util.getDateFromByte(cmsGet.bParams, 0)+" "+util.getTimeFromByte(cmsGet.bParams, 0, true); // 录像开始时间
            String et = util.getDateFromByte(cmsGet.bParams, 4)+" "+util.getTimeFromByte(cmsGet.bParams, 4,true); // 录像结束时间
            int iCoverCount = util.ByteArrayToint(cmsGet.bParams, 8); // 覆盖次数
            byte bHddNum = cmsGet.bParams[12];  // sd卡个数， 0:没有sd卡  >0: 接sd卡
            int iTotalNum = bHddNum==0? 0 : util.ByteArrayToint(cmsGet.bParams, 20);  // 总容量  单位：10M
            int iRemainNum = bHddNum==0? 0 : util.ByteArrayToint(cmsGet.bParams, 24); // 剩余容量 单位：10M
            int iBadNum = bHddNum==0? 0 : util.ByteArrayToint(cmsGet.bParams, 28);    // 坏块数   单位：8K
            String sBadNum = bHddNum==0? "0" : Float.toString(((float) (iBadNum)) / 8f); // bad block

            result = new CardInforResult(ErrorCode.SUCCESS,st,et,iCoverCount,bHddNum,iTotalNum,iRemainNum,iBadNum,sBadNum);
            Log.d("tanhx", "start time:" + st + "\nend time:" + et + "\ntotal num:" + iTotalNum/100f +
                    "GB\nremain num:" + iRemainNum/100f + "GB\nbad num:" + sBadNum + "KB");
        }
        return result;
    }



}
