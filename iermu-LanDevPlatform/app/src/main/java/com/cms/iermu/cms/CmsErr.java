package com.cms.iermu.cms;

/**
 * pcs 返回错误处理
 * Created by wcy on 15/6/26.
 */
public class CmsErr {

    private String strErr;
    private int iErrCode;

    public CmsErr(int iCode, String strerr) {
        iErrCode = iCode;
        strErr = strerr;
    }

    public void setErrValue(int iCode, String strerr){
        iErrCode = iCode;
        strErr = strerr;
    }

    public int getErrCode(){
        return iErrCode;
    }

    public String getErrMsg(){
        return strErr;
    }


}
