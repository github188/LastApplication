package com.iermu.lan.model;

import java.util.ArrayList;

/**
 * Created by zsj on 15/10/20.
 */
public class NasPlayListResult {
    private ErrorCode errorCode;
    private ArrayList<CamRecord> list;

    public NasPlayListResult(ErrorCode errorCode){
        this.errorCode=errorCode;

    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ArrayList<CamRecord> getList() {
        return list;
    }

    public void setList(ArrayList<CamRecord> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "NasPlayListResult{" +
                "errorCode=" + errorCode +
                ", list=" + list +
                '}';
    }
}
