package com.iermu.lan.nas.impl;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iermu.lan.NasDevApi;
import com.cms.iermu.cms.CmsCmdStruct;
import com.cms.iermu.cms.CmsConstants;
import com.cms.iermu.cms.CmsDev;
import com.cms.iermu.cms.CmsErr;
import com.cms.iermu.cms.CmsNetUtil;
import com.cms.iermu.cms.CmsProtocolDef;
import com.cms.iermu.cms.upnp.UpnpUtil;
import com.iermu.lan.model.CamRecord;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.model.NasParamResult;
import com.iermu.lan.model.NasPlayListResult;
import com.iermu.lan.model.NasRec;
import com.iermu.lan.model.NasRecModel;
import com.iermu.lan.model.Result;
import com.iermu.lan.utils.LanUtil;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Created by zsj on 15/10/16.
 */
public class NasDevImpl implements NasDevApi {


    /**
     * 获取samba nas盘共享目录
     *
     * @param strIP
     * @param username
     * @param pwd
     * @return
     */
    @Override
    public Result getSmbFolder(String strIP, String username, String pwd) {
        SmbFile[] domains;
        String SMB_URL = "smb://" + strIP;
        StringBuilder strList = new StringBuilder();
        Result result = new Result(ErrorCode.SUCCESS);

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", username, pwd);
        try {
            SmbFile sm = new SmbFile(SMB_URL, auth);
            domains = sm.listFiles();
            for (int i = 0; i < domains.length; i++) {
                try {
                    SmbFile[] servers = domains[i].listFiles();
                    boolean bwrite = domains[i].canWrite();
                    if (!bwrite) {
                        try {
                            SmbFile testDir = new SmbFile(domains[i].getPath() + "iermutest");
                            testDir.createNewFile();
                            bwrite = true;
                            testDir.delete();
                        } catch (SmbException e) {
                            bwrite = false;
                        }
                    }
                    if (bwrite && domains[i].canRead()) {
                        strList.append(domains[i].getName());
                        strList.deleteCharAt(strList.length() - 1);
                        strList.append("\n");
                    }
                    for (int j = 0; j < servers.length; j++) {
                        if (servers[j].isDirectory() && servers[j].canRead()) {
                            bwrite = servers[j].canWrite();
                            if (!bwrite) {
                                try {
                                    SmbFile testDir = new SmbFile(servers[j].getPath() + "iermutest");
                                    testDir.createNewFile();
                                    bwrite = true;
                                    testDir.delete();
                                } catch (SmbException e) {
                                    bwrite = false;
                                }
                            }
                            if (!bwrite) continue;
                            strList.append(domains[i].getName());  // 添加上级目录
                            strList.append(servers[j].getName());  // 添加下级目录
                            strList.deleteCharAt(strList.length() - 1);
                            strList.append("\n");
                        }
                    }
                } catch (SmbException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            int st = e.getNtStatus();
            if (st == -1073741823) result.setErrorCode(ErrorCode.NAS_IP_ERROR);
            else if (st == -1073741715) result.setErrorCode(ErrorCode.NAS_USERNAME_PWD_ERROR);
            else result.setErrorCode(ErrorCode.EXECUTEFAILED);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (strList.length() > 0) strList.deleteCharAt(strList.length() - 1);
        String data = strList.length() > 0 ? strList.toString() : null;
        result.setResultString(data);
        result.setResultBooean(true);//smb
        return result;
    }

    /**
     * 获取Nfs nas盘共享目录
     *
     */
    @Override
    public Result getNfsPath(Context c, String strDevID, String uid ,String nasIp){
        CmsNetUtil myCmsNet = new CmsNetUtil();
        LanUtil lanutil = new LanUtil();
        Result result = new Result(ErrorCode.SUCCESS);

        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(strDevID);
        if (ip == null) {
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }
        CmsDev dev = lanutil.getCmsDev(ip, uid);
        myCmsNet.setNatConn(true, dev);

        String ret = null;
        CmsCmdStruct cmsData = new CmsCmdStruct();

        cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
        cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NASFIND;
        final String[] strIP = lanutil.split(nasIp, ".");
        byte[] bIP = new byte[4];
        for(int i=0; i<4; i++){
            bIP[i] = (byte) Integer.parseInt(strIP[i]);
        }
        cmsData.bParams = new byte[]{bIP[0], bIP[1], bIP[2], bIP[3]};
        int iRet = -1;
        boolean cmdok = myCmsNet.cmsExecCMD(c, cmsData);
        if(cmdok){
            // 发送查找指令
            long lt = System.currentTimeMillis();

            cmsData.cmsSubCmd =  CmsProtocolDef.PARA3_GETNASRESULT;  // 查询nfs扫描结果
            while(System.currentTimeMillis()-lt < 10000){
                SystemClock.sleep(1000);
                cmsData = new CmsCmdStruct();
                cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
                cmsData.cmsSubCmd = CmsProtocolDef.PARA3_GETNASRESULT;
                cmsData.bParams = new byte[]{bIP[0], bIP[1], bIP[2], bIP[3]};
                    CmsErr err = new CmsErr(CmsConstants.CMS_LAN_CONN_FAIL, "init");
                    cmsData = myCmsNet.getDevParam(c, cmsData, err);
                    if(cmsData!=null && cmsData.bParams!=null) break;

            }


            int iParamLen = (cmsData!=null && cmsData.bParams!=null)? cmsData.bParams.length : 0;
            if(iParamLen > 4){
                //int iLen = cmsUtils.ByteArrayToint(cmsData.bParams, 0, true);
                int iOffset = 4;
                int iCount = 0;
                for(int i=4; i<iParamLen; i++){
                    byte b = cmsData.bParams[i];
                    if(b==0){ // 结束
                        String strPath = new String(cmsData.bParams, iOffset, iCount);
                        Log.d("tanhx", "mount=" + strPath);
                        strPath = strPath.split("|")[0].trim();
                        if(ret==null) ret = strPath;
                        else{
                            ret += ("\n" + strPath);
                        }
                        iCount = 0;
                        iOffset = i+1;
                    }
                    else{
                        iCount++;
                    }
                }

            }
        }
        result.setResultString(ret);
        result.setResultBooean(false);//nfs
        return result;
    }



    /**
     * 获取nas录像列表
     *
     * @param c
     * @param strDevID 设备ID
     * @param uid      百度账号登录后的uid
     * @param strSt    startTime YY-MM-DD HH:mm:SS
     * @param strEt    endTime YY-MM-DD HH:mm:SS
     * @param cmserr
     * @return
     */
    @Override
    public NasPlayListResult getPlayList(Context c, String strDevID, String uid, String strSt, String strEt, CmsErr cmserr) {

        ArrayList<CamRecord> ret = null;
        CmsCmdStruct cmsData = new CmsCmdStruct();
        NasPlayListResult result = new NasPlayListResult(ErrorCode.SUCCESS);
        cmsData.cmsMainCmd = CmsProtocolDef.LAN2_MENU_CTRL;
        cmsData.cmsSubCmd = (byte) CmsProtocolDef.REC_LIST_BY_PARAM;
        LanUtil lanutil = new LanUtil();
        int sTime = lanutil.getCmsTime(strSt, 0);
        int eTime = lanutil.getCmsTime(strEt, 0);
//        int iSt = (int) sTime;
//        int iDay = 1;
        int retCount = 0;
        int prevEndTime = 0;
        HashMap map = new HashMap();
        while (true) {
//            int iEt = lanutil.getCmsMaxTime(strSt, iDay);
//            iEt = eTime;
//            if(iEt>eTime) iEt = eTime;
            cmsData.bParams = new byte[28];
            cmsData.bParams[0] = 0x0;
            for (int i = 1; i < 12; i++) {
                cmsData.bParams[i] = (byte) 0xff;
            }
            byte[] bST = lanutil.getByteFromInt(sTime);
            byte[] bET = lanutil.getByteFromInt(eTime);
            byte[] bType = lanutil.getByteFromInt(0x2);
            byte[] bCh = lanutil.getByteFromInt(1 << (0));
            for (int i = 0; i < 4; i++) {
                cmsData.bParams[i + 12] = bST[i];
                cmsData.bParams[i + 16] = bType[i];    // type 0-普通 1-报警 2-全部
                cmsData.bParams[i + 20] = bCh[i];    // ch
                cmsData.bParams[i + 24] = bET[i];    // 结束时间
            }
            if (cmserr == null) cmserr = new CmsErr(CmsConstants.CMS_LAN_CONN_FAIL, "init");
            String ip = null;
            for (int i = 0; i < 5; i++) {
                ip = new UpnpUtil().getLanDeviceIPByDeviceId(strDevID);
                if (ip != null) break;
            }
            CmsCmdStruct cmsGet = null;
            if (ip == null) {
                result.setErrorCode(ErrorCode.NETEXCEPT);
                return result;
            } else {
                Log.d("tagip:", ip);
                CmsDev dev = lanutil.getCmsDev(ip, uid);
                CmsNetUtil netUtil = new CmsNetUtil();
                netUtil.setNatConn(true, dev);
                cmsGet = netUtil.getDevParam(c, cmsData, cmserr);
            }

            if (cmserr.getErrCode() == 0 && cmsGet != null && cmsGet.bParams != null) {
                NasRecModel[] recs = lanutil.getRecList(cmsGet.bParams);
                int iLen = recs.length;
                if (iLen == 0) break;
                if (iLen > 0) {
                    if (ret == null) ret = new ArrayList<CamRecord>();
                    for (int i = 0; i < iLen; i++) {
                        CamRecord rec = new CamRecord();
                        rec.setStartTime((int) (lanutil.date2Timestamp(recs[i].getRec_start_date() + " " + recs[i].getRec_start_time()) / 1000));
                        if (map.get(rec.getStartTime()) == null) {//解决数据异常导致部分数据重复取问题
                            rec.setEndTime((int) (lanutil.date2Timestamp(recs[i].getRec_start_date() + " " + recs[i].getRec_end_time()) / 1000 - 1));
                            rec.setTimeLen(recs[i].getRec_time_len());
                            rec.setFileLen(recs[i].getRec_file_len());
                            ret.add(rec);
                            map.put(rec.getStartTime(), true);
                        }
                    }
                    if (cmserr.getErrCode() != 0) cmserr.setErrValue(0, Integer.toString(iLen));
                }
                int endTime = lanutil.getCmsTime(lanutil.timeStamp2Date(ret.get(ret.size() - 1).getEndTime()), 0);

                if (sTime == prevEndTime) {//解决犹豫数据异常产生的死循环问题
                    retCount++;
                    prevEndTime = endTime + retCount * 100;
                    sTime = prevEndTime;
                } else {
                    prevEndTime = endTime;
                    sTime = prevEndTime;
                }
            } else {
                String st = lanutil.getTimeFromCms(sTime).substring(0, 11) + "00:00:00";
                sTime = lanutil.getCmsTime(st, 1);
            }
            if (sTime >= eTime) break;
        }
        //cmsLan.closeConn();
        Log.d("tanhx", "nas playlist ret ok! ");
        result.setList(ret);
        return result;
    }

    @Override
    public Result startNasPlay(Context c, String strDevID, String uid, String st, String et) {

        LanUtil lanutil = new LanUtil();
        int iSt = lanutil.getCmsTime(st, 0);
        int iEt = lanutil.getCmsTime(et, 0);
        Result ret = new Result(ErrorCode.SUCCESS);
        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(strDevID);

        CmsNetUtil netUtil = new CmsNetUtil();
        if (ip == null) {
            ret.setErrorCode(ErrorCode.NETEXCEPT);
            return ret;
        } else {
            CmsDev dev = lanutil.getCmsDev(ip, uid);
            netUtil.setNatConn(true, dev);
        }

        Short iPort = getIPort(c, netUtil);

        // 播放地址
        String strUrl = "rtmp://" + ip + ":" + iPort + "/live/" + strDevID;
        // 先停止播放
//    setLanVodplay(c, C, uid, 3, 0, 0);
        boolean m_bLanPlay = setLanVodplay(c, strDevID, uid, 1, iSt, iEt);

        if (m_bLanPlay)
            ret.setResultString(strUrl);
        else
            ret.setErrorCode(ErrorCode.EXECUTEFAILED);// 设置失败

        return ret;
    }

    //@Override
    public Result stopNasPlay(Context c, String strDevID, String uid) {
        CmsNetUtil myCmsNet = new CmsNetUtil();
        LanUtil lanutil = new LanUtil();
        Result result = new Result(ErrorCode.SUCCESS);

        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(strDevID);
        if (ip == null) {
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }
        CmsDev dev = lanutil.getCmsDev(ip, uid);
        myCmsNet.setNatConn(true, dev);

        CmsCmdStruct cmsData = new CmsCmdStruct();
        cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
        cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NASPLAYPARA;
        cmsData.bParams = null;
        byte[] bRtmpID = strDevID.getBytes();
        byte[] bcmd = lanutil.htonl(3);  // 0x01：准备播放  0x02：更改播放时间   0x03：停止播放
        cmsData.bParams = new byte[48];
        for (int i = 0; i < 48; i++) {
            cmsData.bParams[i] = 0;
        }
        System.arraycopy(bcmd, 0, cmsData.bParams, 0, bcmd.length);
        System.arraycopy(bRtmpID, 0, cmsData.bParams, 4, bRtmpID.length); // 8字节 nas rec
        byte[] bSt = lanutil.htonl(0);
        byte[] bEt = lanutil.htonl(0);
        System.arraycopy(bSt, 0, cmsData.bParams, 40, bSt.length);
        System.arraycopy(bEt, 0, cmsData.bParams, 44, bEt.length);
        boolean ret = myCmsNet.setDevParam(c, cmsData);
        if (!ret) {
            result.setErrorCode(ErrorCode.EXECUTEFAILED);
            return result;
        }

        return result;
    }

    /**
     * 获取nas设置
     *
     * @param c
     * @param strDevID
     * @param uid
     * @return NasParamResult.map
     * ip：nas设置的ip
     * uName：nas用户名
     * pwd：nas密码
     * nasPath：nas存储路径
     * size：nas存储容量（G）
     */
    @Override
    public NasParamResult getNasParam(Context c, String strDevID, String uid) {
        CmsNetUtil myCmsNet = new CmsNetUtil();
        LanUtil lanutil = new LanUtil();
        NasParamResult result = new NasParamResult(ErrorCode.SUCCESS);

        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(strDevID);
        if (ip == null) {
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }
        CmsDev dev = lanutil.getCmsDev(ip, uid);
        myCmsNet.setNatConn(true, dev);


        CmsCmdStruct cmsData = new CmsCmdStruct();
        cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
        cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NASCONF;
        CmsCmdStruct cmsget = myCmsNet.getDevParam(c, cmsData, new CmsErr(-1, "init"));
        if (cmsget == null) {
            result.setErrorCode(ErrorCode.EXECUTEFAILED);
            return result;
        }
        byte[] b = cmsget.bParams;
        byte[] intV = new byte[]{(byte) 192, (byte) 168, 1, 0};
        for (int i = 134; i < 138; i++) {
            if (b[i] == 0) break;
            intV[i - 134] = b[i];
        }
        String strIp = null;
        try {
            strIp = InetAddress.getByAddress(intV).toString();
            if (strIp.substring(0, 1).equals("/")) {
                strIp = strIp.substring(1);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        boolean bNasON = (b[0]&1)==1;
        boolean isSmb = (b[0]>>7&1)==1;

        String etUser = lanutil.getByteToString(b, 1, 33);
        String etPwd = lanutil.getByteToString(b, 34, 33);
        String strNasPath = lanutil.getByteToString(b, 67, 51);
        int iSize = lanutil.ByteArrayToint(b, 138);
        if (iSize < 4) iSize = 4;
        HashMap map = new HashMap();
        map.put("bNasON", bNasON);
        map.put("isSmb", isSmb);
        map.put("ip", strIp);
        map.put("uName", etUser);
        map.put("pwd", etPwd);
        map.put("nasPath", strNasPath);
        map.put("size", iSize+"");
        result.setMap(map);
        return result;
    }

    //获取端口号
    private Short getIPort(Context c, CmsNetUtil netUtil) {
        CmsCmdStruct cmsData = new CmsCmdStruct();
        LanUtil lanutil = new LanUtil();
        cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
        cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NASPLAYPARA;
        cmsData.bParams = null;
        // 先获取当前nas播放参数信息
        CmsErr cmserr = new CmsErr(-1, "init");
        CmsCmdStruct cmsGet = netUtil.getDevParam(c, cmsData, cmserr);
        if (cmsGet == null) {
            return null;
        }

        Short iPort = lanutil.decodeShort(cmsGet.bParams, 0);  // 获取播放端口
        return iPort;

    }

    /**
     * 让设备准备好播放相关视频
     *
     * @param c
     * @param devID  设备id
     * @param uid    userid
     * @param status 0x01：准备播放  0x02：更改播放时间   0x03：停止播放
     * @param iSt    开始时间戳
     * @param iEt    结束时间戳
     * @return
     */
    private boolean setLanVodplay(Context c, String devID, String uid, int status, int iSt, int iEt) {

        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(devID);
        LanUtil lanutil = new LanUtil();
        CmsNetUtil netUtil = new CmsNetUtil();
        CmsDev dev = lanutil.getCmsDev(ip, uid);
        netUtil.setNatConn(true, dev);

        CmsCmdStruct cmsData = new CmsCmdStruct();
        cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
        cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NASPLAYPARA;
        cmsData.bParams = new byte[48];
        byte[] bRtmpID = devID.getBytes();
        byte[] bcmd = lanutil.htonl(status);  // 0x01：准备播放  0x02：更改播放时间   0x03：停止播放
        cmsData.bParams = new byte[48];
        for (int i = 0; i < 48; i++) {
            cmsData.bParams[i] = 0;
        }
        System.arraycopy(bcmd, 0, cmsData.bParams, 0, bcmd.length);
        System.arraycopy(bRtmpID, 0, cmsData.bParams, 4, bRtmpID.length); // 8字节 nas rec
        byte[] bSt = lanutil.htonl(iSt);
        byte[] bEt = lanutil.htonl(iEt);
        System.arraycopy(bSt, 0, cmsData.bParams, 40, bSt.length);
        System.arraycopy(bEt, 0, cmsData.bParams, 44, bEt.length);
        boolean re = netUtil.setDevParam(c, cmsData);
        return re;
    }


    /**设置nas参数
     *
     * @param c
     * @param strDevID  设备id
     * @param uid    userid
     * @param swNasCheck nas开关
     * @param etUser    路由器用户名
     * @param etPwd    路由器密码
     * @param spPath    视屏存储路径名
     * @param etIp    路由器ip
     * @param etSize    存储目录空间大小（G）
     * @return
     */
    @Override
    public Result setNasParam(Context c, String strDevID, String uid, boolean swNasCheck, String etUser, String etPwd, String spPath,String etIp,int etSize,boolean isSmb) {

        CmsNetUtil myCmsNet = new CmsNetUtil();
        LanUtil lanutil = new LanUtil();
        Result result = new Result(ErrorCode.SUCCESS);

        String ip = new UpnpUtil().getLanDeviceIPByDeviceId(strDevID);
        if (ip == null) {
            result.setErrorCode(ErrorCode.NETEXCEPT);
            return result;
        }
        CmsDev dev = lanutil.getCmsDev(ip, uid);
        myCmsNet.setNatConn(true, dev);


        CmsCmdStruct cmsData = new CmsCmdStruct();

        cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
        cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NASCONF;
        int s = lanutil.htonl(etSize).length+138;
        cmsData.bParams = new byte[s];
        for (int i = 0; i < s; i++) {
            cmsData.bParams[i] = 0;
        }
        cmsData.bParams[0] = (byte) (swNasCheck ? 1 : 0);
//        if (bSmb) cmsData.bParams[0] |= (1 << 7);
//        else
        if(isSmb) cmsData.bParams[0] |= (1<<7);
        else cmsData.bParams[0] |= (1<<1);
//        cmsData.bParams[0] |= (1 << 7);
        byte[] bUser = etUser.trim().getBytes();
        byte[] bPwd = etPwd.trim().getBytes();
        System.arraycopy(bUser, 0, cmsData.bParams, 1, bUser.length);
        System.arraycopy(bPwd, 0, cmsData.bParams, 34, bPwd.length);
        byte[] bPath = spPath.trim().getBytes();
        System.arraycopy(bPath, 0, cmsData.bParams, 67, bPath.length);
        byte[] bFolder = strDevID.getBytes();
        System.arraycopy(bFolder, 0, cmsData.bParams, 118, bFolder.length);
        String[] strT = lanutil.split(etIp, ".");
        cmsData.bParams[134] = (byte) Integer.parseInt(strT[0]);
        cmsData.bParams[135] = (byte) Integer.parseInt(strT[1]);
        cmsData.bParams[136] = (byte) Integer.parseInt(strT[2]);
        cmsData.bParams[137] = (byte) Integer.parseInt(strT[3]);
        byte[] bSize= lanutil.htonl(etSize);
        System.arraycopy(bSize, 0, cmsData.bParams, 138, bSize.length);

        boolean ret = myCmsNet.setDevParam(c, cmsData);
        if(!ret){
            result.setResultBooean(ret);
            return result;
        }

        // 发送查找指令
        boolean bOK = false;
        long lt = System.currentTimeMillis();
        cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NASCONF;  // 查询mount结果
        while (System.currentTimeMillis() - lt < 5000)

        {
            SystemClock.sleep(200);
            cmsData = new CmsCmdStruct();
            cmsData.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;
            cmsData.cmsSubCmd = CmsProtocolDef.PARA3_NASCONF;
            cmsData.bParams = null;
//            if (cmsLan != null) {
            CmsErr cmserr = new CmsErr(CmsConstants.CMS_LAN_CONN_FAIL, "init");
                cmsData = myCmsNet.getDevParam(null, cmsData, cmserr);
//            } else {
//                int iRet = getDevData(strDevID, strToken, cmsData);
//            }
            if (cmsData != null && cmsData.bParams != null && cmsData.bParams.length > 140) {
                final boolean bNasON = (cmsData.bParams[0] & 1) == 1;
                boolean bStarting = (cmsData.bParams[0] >> 3 & 1) == 1;
                boolean bStart = (cmsData.bParams[0] >> 4 & 1) == 1;
                Log.d("tanhx", "nas on=" + (cmsData.bParams[0] & 0xff));
//                if (!swNasCheck) {  // 用户设置关闭
//                    bOK = !bNasON;
//                } else {
//                    if (!bStarting) bOK = bStart;
//                }
                bOK = swNasCheck == bNasON;
                if (bOK ) break;
//                if (bOK || !bStarting) break;
            }
        }
//        NasParamResult nasParam = getNasParam(c, strDevID, uid);
//        Map map = nasParam.getMap();
        result.setResultBooean(bOK);
        return result;
    }

}
