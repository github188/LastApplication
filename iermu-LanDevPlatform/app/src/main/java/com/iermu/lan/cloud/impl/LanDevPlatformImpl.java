package com.iermu.lan.cloud.impl;

import android.content.Context;
import android.util.Log;

import com.cms.iermu.cms.CmsProtocolDef;
import com.iermu.lan.LanDevPlatformApi;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.model.Result;
import com.cms.iermu.cms.CmsCmdStruct;
import com.cms.iermu.cms.CmsDev;
import com.cms.iermu.cms.CmsErr;
import com.cms.iermu.cms.CmsNetUtil;
import com.cms.iermu.cms.upnp.UpnpUtil;
import com.iermu.lan.utils.LanUtil;


/**
 * 云台局域网协议
 * Created by zsj on 15/10/14.
 */
public class LanDevPlatformImpl implements LanDevPlatformApi {

    static {
        try {
            System.loadLibrary("cmsNative");
        } catch (UnsatisfiedLinkError e) {
            Log.e("load library failed", e.toString());
        }
    }

    /**
     * 开启云台自动平扫
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    @Override
    public Result openDevRotate(Context context,String devId,String uid){
        LanUtil util  = new LanUtil();
        Result result= new Result(ErrorCode.SUCCESS);
        CmsCmdStruct cmsCmdStruct = util.getOpenDevRotateStruct();
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            boolean re= netUtil.setDevParam(context, cmsCmdStruct);
            result.setErrorCode(re?ErrorCode.SUCCESS:ErrorCode.EXECUTEFAILED);
        }
        return result;
    }
    /**
     * 关闭云台自动平扫
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    @Override
    public Result closeDevRotate(Context context,String devId,String uid) {
        LanUtil util  = new LanUtil();
        Result result= new Result(ErrorCode.SUCCESS);
        CmsCmdStruct cmsCmdStruct = util.getCloseDevRotateStruct();
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            boolean re= netUtil.setDevParam(context, cmsCmdStruct);
            result.setErrorCode(re?ErrorCode.SUCCESS:ErrorCode.EXECUTEFAILED);
        }
        return result;
    }

    /**
     * 添加云台预置点
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @param number 预置点序号
     */
    @Override
    public Result addDevPresetPoint(Context context,String devId,String uid,int number) {
        LanUtil util  = new LanUtil();
        Result result= new Result(ErrorCode.SUCCESS);
        CmsCmdStruct cmsCmdStruct = util.getAddDevPresetPointStruct(number);
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            boolean re= netUtil.setDevParam(context, cmsCmdStruct);
            result.setErrorCode(re?ErrorCode.SUCCESS:ErrorCode.EXECUTEFAILED);
        }
        return result;

    }

    /**
     * 执行云台预置点
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    @Override
    public Result toDevPresetPoint(Context context,String devId,String uid,int number) {
        LanUtil util  = new LanUtil();
        Result result= new Result(ErrorCode.SUCCESS);
        CmsCmdStruct cmsCmdStruct = util.getToDevPresetPointStruct(number);
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            boolean re= netUtil.setDevParam(context, cmsCmdStruct);
            result.setErrorCode(re?ErrorCode.SUCCESS:ErrorCode.EXECUTEFAILED);
        }
        return result;
    }

    /**
     * 云台按指定坐标移动
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @param xSrc 起始点X坐标
     * @param ySrc 起始点y坐标
     * @param xDest 终点X坐标
     * @param yDest 终点y坐标
     * @return Result.int 0:没有到边界；1：左边界；2右边界，3，上边界，4下边界；
     */
    @Override
    public Result setDevMovePoint(Context context, String devId, String uid, int xSrc, int ySrc, int xDest, int yDest) {
        LanUtil util  = new LanUtil();
        Result result= new Result(ErrorCode.SUCCESS);
        CmsCmdStruct cmsCmdStruct = util.getSetDevMoveStruct(xSrc, ySrc, xDest, yDest);
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        int s = 0;
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            CmsCmdStruct re= netUtil.getDevParam(context, cmsCmdStruct, new CmsErr(-1, ""));
            if(re==null) {
                result.setErrorCode(ErrorCode.EXECUTEFAILED);
                return result;
            }
            int iLen = re.paramLen==0? re.bParams.length : re.paramLen;
            for(int i=0; i<4; i++){
                if(((iLen>>i)&1)==1){  //“0”是没有边界“1”是到边界 （bit0--左边界；bit1--右边界；bit2--上边界；bit3--下边界，上下不提示，“转到头啦，换个方向试试吧”）
                    s=i+1;
                    break;
                }
            }
        }
        result.setResultInt(s);
        return result;
    }

    /**
     * 检测云台是否处于平扫状态
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    @Override
    public Result isRotate(Context context, String devId, String uid) {
        LanUtil util  = new LanUtil();
        Result result= new Result(ErrorCode.SUCCESS);
        CmsCmdStruct ptzXY = util.getIsRotateStruct();
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            CmsCmdStruct cmsGet =  netUtil.getDevParam(context, ptzXY, new CmsErr(1, ""));
            if(cmsGet==null||cmsGet.bParams.length==0){
                result.setErrorCode(ErrorCode.EXECUTEFAILED);
                return result;
            }
            int df = cmsGet.bParams[4];
            result.setResultBooean(cmsGet.bParams[4] == 1);
        }
        return result;
    }

    /**
     * 检测云台是否支持坐标位移
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    @Override
    public Result isSupportXYMove(Context context, String devId, String uid) {

        LanUtil util  = new LanUtil();
        Result result= new Result(ErrorCode.SUCCESS);
        CmsCmdStruct ptzXY = util.getIsSupportXYMoveStruct();
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            CmsCmdStruct cmsGet =  netUtil.getDevParam(context, ptzXY, new CmsErr(1, ""));
            result.setResultBooean(cmsGet.bParams[1] == 1);
        }
        return result;
    }

    /**
     * 检测是否有云台
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
   // @Override
    public Result isSupportPlatform(Context context, String devId, String uid) {
        LanUtil util  = new LanUtil();
        Result result= new Result(ErrorCode.SUCCESS);
        CmsCmdStruct cmsCmd = util.getIsSupportPlatformStruct();
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devId);
        if(ip==null){
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }else {
            CmsDev dev = util.getCmsDev(ip, uid);
            CmsNetUtil netUtil = new CmsNetUtil();
            netUtil.setNatConn(true, dev);
            CmsCmdStruct cmsGet =  netUtil.getDevParam(context, cmsCmd, new CmsErr(1, ""));
            result.setResultBooean(cmsGet.bParams[0] == 0);
        }
        return result;
    }

    /**
     * 获取局域网直播路径
     * @param context
     * @param strDevId 设备号
     * @param uid 百度账号登录后的uid
     * @param version 固件版本号(例如: 6.123_100_20_2015-12-1  前面的6.123为固件大版本号，20为小版本号，需拼在一起)
     *
     */
    @Override
    public Result getLanPlayUrl(Context context, String strDevId, String uid, String version){
        boolean m_bLanPlay=false;
        Result result = new Result(ErrorCode.SUCCESS);
        try{
            LanUtil lanUtil  = new LanUtil();
            String ip = new UpnpUtil().getLanDeviceIPByDeviceId(strDevId);
            if(ip==null){
                result.setErrorCode(ErrorCode.NETEXCEPT);
                return result;
            }
            int ver = 0;
            if(!"".equals(version)){
                ver = Integer.parseInt(version.split("_")[0].replace(".", "") + version.split("_")[2]);//截取固件版本号
            }
//            if(ver<=612320){
                CmsDev dev = lanUtil.getCmsDev(ip, uid);
                CmsNetUtil netUtil = new CmsNetUtil();
                netUtil.setNatConn(true, dev);

                CmsCmdStruct cmsData = new CmsCmdStruct();
                cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
                cmsData.cmsSubCmd = CmsProtocolDef.PARA3_RTMPLOCALPLAY;
                byte[] bRtmpID = strDevId.getBytes();
                byte[] bcmd = lanUtil.htonl(5);  // 控制局域网播放 7：单局域网(调试模式)  5：局域网+百度云
                cmsData.bParams = new byte[36];
                System.arraycopy(bcmd, 0, cmsData.bParams, 0, bcmd.length);
                System.arraycopy(bRtmpID, 0, cmsData.bParams, 4, bRtmpID.length);
                m_bLanPlay = netUtil.setDevParam(context, cmsData);
                if(!m_bLanPlay){
                    result.setErrorCode(ErrorCode.EXECUTEFAILED);
                    return result;
                }
//            }

            // 局域网播放地址
            String strUrl = "rtmp://" + ip + "/live/" + strDevId;

            result.setResultString(strUrl);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }


}
