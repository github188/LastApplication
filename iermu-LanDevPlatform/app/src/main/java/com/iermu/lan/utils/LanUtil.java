package com.iermu.lan.utils;

import com.cms.iermu.cms.CmsCmdStruct;
import com.cms.iermu.cms.CmsDev;
import com.cms.iermu.cms.CmsNetUtil;
import com.cms.iermu.cms.CmsProtocolDef;
import com.iermu.lan.model.NasRecModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created by zsj on 15/10/14.
 */
public class LanUtil {
    public static final int CMS_DATEYEAR_BASE = 1984;

    /**
     * 构造"setNatConn"函数的参数
     *
     * @param ip  设备在局域网ip
     * @param uid 百度账号登录后的uid
     */
    public CmsDev getCmsDev(String ip, String uid) {
        CmsDev dev = new CmsDev();
        dev.type = LanConfig.CMS_BD_IERMU;
        dev.port = LanConfig.DEV_PORT;
        dev.username = LanConfig.USER_NAME;
        dev.password = uid;
        dev.url = ip;
        dev.nat_server = LanConfig.NAT_SERVER;
        return dev;
    }

    //开启云台自动平扫
    public CmsCmdStruct getOpenDevRotateStruct() {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = 71;
        cmsCmdStruct.cmsSubCmd = 11;
        cmsCmdStruct.bParams = new byte[]{1, 4, 1};
        return cmsCmdStruct;
    }

    // 关闭云台自动平扫
    public CmsCmdStruct getCloseDevRotateStruct() {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = 71;
        cmsCmdStruct.cmsSubCmd = 12;
        cmsCmdStruct.bParams = new byte[]{1, 4, 1};
        return cmsCmdStruct;
    }


    /**
     * 添加云台预置点
     *
     * @param number 预置点号
     */
    public CmsCmdStruct getAddDevPresetPointStruct(int number) {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = 71; //
        cmsCmdStruct.cmsSubCmd = 18;  //
        cmsCmdStruct.bParams = new byte[]{1, (byte) number, 1};//[0]:xx [1]:bb [2]:nn
        return cmsCmdStruct;
    }

    /**
     * 执行云台预置点
     */
    public CmsCmdStruct getToDevPresetPointStruct(int number) {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = 71;
        cmsCmdStruct.cmsSubCmd = 19;
        cmsCmdStruct.bParams = new byte[]{1, (byte) number, 1};
        return cmsCmdStruct;
    }

    /**
     * 云台按指定坐标移动
     *
     * @param xSrc  起始点X坐标
     * @param ySrc  起始点y坐标
     * @param xDest 终点X坐标
     * @param yDest 终点y坐标
     */
    public CmsCmdStruct getSetDevMoveStruct(int xSrc, int ySrc, int xDest, int yDest) {
//        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
//        cmsCmdStruct.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
//        cmsCmdStruct.cmsSubCmd = CmsProtocolDef.PARA3_PTZOFFSET;
//        cmsCmdStruct.bParams = new byte[]{1, (byte) xSrc, (byte) ySrc, (byte) xDest, (byte) yDest, 1};
        CmsCmdStruct cmsCmd = new CmsCmdStruct();
        cmsCmd.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_SET;
        cmsCmd.cmsSubCmd = CmsProtocolDef.PARA3_PTZOFFSET;
        LanUtil lanUtil = new LanUtil();
        cmsCmd.bParams = new byte[16];
        byte[] bX = lanUtil.htonl(xSrc);
        byte[] bY = lanUtil.htonl(ySrc);
        byte[] bX_e = lanUtil.htonl(xDest);
        byte[] bY_e = lanUtil.htonl(yDest);
        System.arraycopy(bX, 0, cmsCmd.bParams, 0, 4);
        System.arraycopy(bY, 0, cmsCmd.bParams, 4, 4);
        System.arraycopy(bX_e, 0, cmsCmd.bParams, 8, 4);
        System.arraycopy(bY_e, 0, cmsCmd.bParams, 12, 4);
        //Log.d("tanhx", "ptz x=" + iX + ", y=" + iY + ",ex=" + iX_e + ",ey=" + iY_e);
        return cmsCmd;
//        return cmsCmdStruct;
    }


    /**
     * 检测云台是否处于平扫状态
     */
    public CmsCmdStruct getIsRotateStruct() {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = 74;
        cmsCmdStruct.cmsSubCmd = 66;
        return cmsCmdStruct;
    }

    /**SupportPlatform
     * 检测云台是否支持坐标位移
     */
    public CmsCmdStruct getIsSupportXYMoveStruct() {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = 74;
        cmsCmdStruct.cmsSubCmd = 66;
        return cmsCmdStruct;
    }

    /**
     * 检测是否支持云台
     */
    public CmsCmdStruct getIsSupportPlatformStruct() {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = 71;
        cmsCmdStruct.cmsSubCmd = 26;
        return cmsCmdStruct;
    }

    /**
     * 空气胶囊温湿度
     */
    public CmsCmdStruct getAirCapsuleStruct() {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = 74;
        cmsCmdStruct.cmsSubCmd = 74;
        return cmsCmdStruct;
    }

    /**
     * 获取存储卡信息
     */
    public CmsCmdStruct getCardInforStruct() {
        CmsCmdStruct cmsCmdStruct = new CmsCmdStruct();
        cmsCmdStruct.cmsMainCmd = CmsProtocolDef.LAN2_RUNPARA3_GET;;
        cmsCmdStruct.cmsSubCmd = CmsProtocolDef.PARA3_HDDINFO;
        return cmsCmdStruct;
    }


    /**
     * 根据cms时间压缩算法得到时间的int值
     *
     * @param strTime YY-MM-DD HH:mm:SS
     * @return int
     */
    public int getCmsTime(String strTime, int iOffsetDay) {
        // Log.d("tanhx1", "cms input time = " + strTime);
        int ret = -1;
        String[] str = split(strTime, " ");
        if (str.length > 1) {
            String[] strD = split(str[0].trim(), "-");
            String[] strT = split(str[1].trim(), ":");
            if (strD.length == 3 && strT.length == 3) {
                try {
                    int y = Integer.parseInt(strD[0]);
                    int m = Integer.parseInt(strD[1]);
                    int d = Integer.parseInt(strD[2]) + iOffsetDay;
                    int h = Integer.parseInt(strT[0]);
                    int mi = Integer.parseInt(strT[1]);
                    int s = Integer.parseInt(strT[2]);
                    ret = (((y - CMS_DATEYEAR_BASE) & 0x3f) << 26)
                            | ((m & 0xf) << 22) | ((d & 0x1f) << 17)
                            | ((h & 0x1f) << 12) | ((mi & 0x3f) << 6)
                            | ((s & 0x3f));
                } catch (Exception e) {

                }
            }
        }
        // Log.d("tanhx", "cms time= " + ret);
        return ret;
    }

    public static String getTimeFromCms(int t) {
        int iY = (((t >> 26) & 0x3f) + CMS_DATEYEAR_BASE);
        int iM = ((t >> 22) & 0xf);
        int iD = ((t >> 17) & 0x1f);
        int iH = ((t >> 12) & 0x1f);
        int im = ((t >> 6) & 0x3f);
        int is = ((t) & 0x3f);
        return String.format("%04d", iY) + "-" + String.format("%02d", iM)
                + "-" + String.format("%02d", iD) + " "
                + String.format("%02d", iH) + ":" + String.format("%02d", im)
                + ":" + String.format("%02d", is);
    }

    /**
     * Split string into multiple strings
     *
     * @param original  : Original string
     * @param separator : Separator string in original string
     * @return Splitted string array
     */
    public String[] split(String original, String separator) {
        if (original == null) return null;
        Vector<String> nodes = new Vector<String>();
        nodes.removeAllElements();
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            nodes.copyInto(result);
        }
        return result;
    }

    public int getCmsMaxTime(String strTime, int iOffsetDay) {
        // Log.d("tanhx1", "cms input time = " + strTime);
        int ret = -1;
        String[] str = split(strTime, " ");
        if (str.length > 1) {
            String[] strD = split(str[0].trim(), "-");
            String[] strT = split(str[1].trim(), ":");
            if (strD.length == 3 && strT.length == 3) {
                try {
                    int y = Integer.parseInt(strD[0]);
                    int m = Integer.parseInt(strD[1]);
                    int d = Integer.parseInt(strD[2]) + iOffsetDay;
                    int h = 0;
                    int mi = 0;
                    int s = 0;
                    ret = (((y - CMS_DATEYEAR_BASE) & 0x3f) << 26)
                            | ((m & 0xf) << 22) | ((d & 0x1f) << 17)
                            | ((h & 0x1f) << 12) | ((mi & 0x3f) << 6)
                            | ((s & 0x3f));
                } catch (Exception e) {

                }
            }
        }
        // Log.d("tanhx", "cms time= " + ret);
        return ret;
    }

    public byte[] getByteFromInt(int i) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (i & 0xFF);
        ret[1] = (byte) (0xFF & i >> 8);
        ret[2] = (byte) (0xFF & i >> 16);
        ret[3] = (byte) (0xFF & i >> 24);
        return ret;
    }
    public static String timeStamp2Date(long timestampString) {
        SimpleDateFormat dspFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dspFmt.format(new java.util.Date(timestampString * 1000));
        return date;
    }

    public NasRecModel[] getRecList(byte[] b) {
        if (b == null) return null;
        NasRecModel[] ret = null;
        int iReclistLen = byteArrayToint(b, 0, false);
        if (iReclistLen > 0) {
            ret = new NasRecModel[iReclistLen];
            for (int i = 0; i < iReclistLen; i++) {
                ret[i] = new NasRecModel();
                ret[i].setRec_ch_id(byteArrayToint(b, 4, false));
                ret[i].setRec_date(getDateFromByte(b, 8));
            }
            int iS = 12;
            int iSegLen = 4 * iReclistLen;
            for (int i = 0; i < iReclistLen; i++) {
                ret[i].setRec_type(b[iS + i]);
                ret[i].setRec_cms_time(byteArrayToint(b, iS + iSegLen + 4 * i, false));
                ret[i].setRec_start_date(getDateFromByte(b, iS + iSegLen + 4 * i, false));
                ret[i].setRec_start_time(getTimeFromByte(b, iS + iSegLen + 4 * i, false));
                ret[i].setRec_time_len(getTimeLen(b, iS + 2 * iSegLen + 4 * i));
                ret[i].setRec_end_time(getNewTime(ret[i].getRec_start_time(), ret[i].getRec_time_len()));
                ret[i].setRec_file_len(String.format("%1.2f", (float) (1024f * byteArrayToFloat(b, iS + 3 * iSegLen + 4 * i))));
                ret[i].setRec_ch_id(byteArrayToint(b, iS + 4 * iSegLen + 4 * i, false));
                ret[i].setRec_hdd_id(byteArrayToint(b, iS + 5 * iSegLen + 4 * i, false));
                ret[i].setRec_lba_id(byteArrayToint(b, iS + 6 * iSegLen + 4 * i, false));
            }
        }
        return ret;
    }

    public int byteArrayToint(byte[] b, int iStart, boolean bBig) {
        int ret = 0;
        if (bBig) {
            for (int i = iStart; i < iStart + 4; i++) {
                ret = ret << 8 | (0xFF & b[i]);
            }
        } else {
            for (int i = iStart + 3; i > iStart - 1; i--) {
                ret = ret << 8 | (0xFF & b[i]);
            }
        }
        return ret;
    }

    public int byteArrayToint(byte[] b, int iStart) {
        int ret = 0;
        for (int i = iStart; i < iStart + 4; i++)
            ret = ret << 8 | (0xFF & b[i]);
        return ret;
    }

    public String getDateFromByte(byte[] b, int iStart) {
        int ibuildDate = byteArrayToint(b, iStart);
        int iY = (((ibuildDate >> 26) & 0x3f) + CMS_DATEYEAR_BASE);
        int iM = ((ibuildDate >> 22) & 0xf);
        int iD = ((ibuildDate >> 17) & 0x1f);
        String ret = Integer.toString(iY) + "-" + Integer.toString(iM) + "-"
                + Integer.toString(iD);
        return ret;
    }
    public String getDateFromByte(byte[] b, int iStart, boolean bBig) {
        int ibuildDate = byteArrayToint(b, iStart, bBig);
        int iY = (((ibuildDate >> 26) & 0x3f) + CMS_DATEYEAR_BASE);
        int iM = ((ibuildDate >> 22) & 0xf);
        int iD = ((ibuildDate >> 17) & 0x1f);
        String ret = Integer.toString(iY) + "-" + String.format("%02d", iM)
                + "-" + String.format("%02d", iD);
        return ret;
    }
    public String getTimeFromByte(byte[] b, int iStart, boolean bBig) {
        int ibuildDate = byteArrayToint(b, iStart, bBig);
        int iH = (ibuildDate >> 12) & 0x1f;
        int im = (ibuildDate >> 6) & 0x3f;
        int iS = ibuildDate & 0x3f;
        String ret = String.format("%02d", iH) + ":"
                + String.format("%02d", im) + ":" + String.format("%02d", iS);
        return ret;
    }

    /**
     * 转化时间值
     * @param b byte数组
     * @param iT_l 开始下标
     * @return HH:mm:SS
     */
    public String getTimeLen(byte[] b, int iT_l){
        int iTimeLen = byteArrayToint(b, iT_l, false);
        int iS = iTimeLen % 60;
        int im = iTimeLen / 60;
        int iH = im / 60;
        im = im % 60;
        String ret = String.format("%02d", iH) + ":" + String.format("%02d", im) + ":" + String.format("%02d", iS);
        return ret;
    }

    /**
     * 得到新时间，不包含日期
     * @param strStart 开始时间 HH:mm:SS
     * @param strTimelen 增加时间 HH:mm:SS
     * @return 新时间 HH:mm:SS
     */
    public String getNewTime(String strStart, String strTimelen){
        String ret = strStart;
        String[] strT = split(strStart.trim(), ":");
        String[] strTa = split(strTimelen.trim(), ":");
        if(strT.length == 3 && strTa.length == 3){
            int[] iT = new int[3];
            for(int i=0; i<3; i++){
                iT[i] = Integer.parseInt(strT[i]);
                iT[i] += Integer.parseInt(strTa[i]);
            }
            if(iT[2] >= 60){
                iT[2] -= 60;
                iT[1] += 1;
            }
            if(iT[1] >= 60){
                iT[1] -= 60;
                iT[0] += 1;
            }
            ret = String.format("%02d", iT[0]) + ":" +
                    String.format("%02d", iT[1]) + ":" +
                    String.format("%02d", iT[2]);
        }
        return ret;
    }

    public float byteArrayToFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        // System.out.println(accum);
        return Float.intBitsToFloat(accum);
    }

    public float byteArrayToFloat(byte[] b, int iStart) {
        int accum = 0;
        accum = accum | (b[iStart + 0] & 0xff) << 0;
        accum = accum | (b[iStart + 1] & 0xff) << 8;
        accum = accum | (b[iStart + 2] & 0xff) << 16;
        accum = accum | (b[iStart + 3] & 0xff) << 24;
        // System.out.println(accum);
        return Float.intBitsToFloat(accum);
    }

    public long date2Timestamp(String strDate){
        long ret = 0;
        SimpleDateFormat dspFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = dspFmt.parse(strDate);
            ret = d.getTime();
            //Log.d("tanhx", "time stamp is " + ret);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }
    public static byte[] htonl(int i) {
        byte[] ret = new byte[4];
        ret[3] = (byte) (i & 0xFF);
        ret[2] = (byte) (0xFF & i >> 8);
        ret[1] = (byte) (0xFF & i >> 16);
        ret[0] = (byte) (0xFF & i >> 24);
        return ret;
    }

    public static byte[] htons(short i) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (0xFF & i >> 8);
        ret[1] = (byte) (i & 0xFF);
        return ret;
    }

    public short decodeShort(byte[] acPack, int iInc, int intLen) {
        int m = 0;
        int k = 0;
        for (int j = iInc; j < intLen + iInc; j++) {
            int i = (0xFF & acPack[j]) << k * 8;
            k++;
            m = i + m;
        }
        return (short) m;
    }

    public short decodeShort(byte[] b, int iStart) {
        short ret = 0;
        for (int i = iStart; i < iStart + 2; i++)
            ret = (short) (ret << 8 | (0xFF & b[i]));
        return ret;
    }

    public int ByteArrayToint(byte[] b, int iStart) {
        int ret = 0;
        for (int i = iStart; i < iStart + 4; i++)
            ret = ret << 8 | (0xFF & b[i]);
        return ret;
    }

    public int ByteArrayToint(byte[] b, int iStart, boolean bBig) {
        int ret = 0;
        if (bBig) {
            for (int i = iStart; i < iStart + 4; i++) {
                ret = ret << 8 | (0xFF & b[i]);
            }
        } else {
            for (int i = iStart + 3; i > iStart - 1; i--) {
                ret = ret << 8 | (0xFF & b[i]);
            }
        }
        return ret;
    }

    public String getByteToString(byte[] b,int start,int length){
        if(b[start]==0) return "";
        int i=start;
        for (i = start; i < start+length; i++) {
            if(b[i]==0) break;
        }
        String str = new String(b, start, i-start);
        return str;
    }
}
