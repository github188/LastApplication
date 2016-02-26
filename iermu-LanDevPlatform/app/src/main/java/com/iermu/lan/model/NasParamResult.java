package com.iermu.lan.model;

import java.util.HashMap;

/**
 * Created by zsj on 15/10/22.
 */
public class NasParamResult {
    private ErrorCode errorCode;
    private HashMap map;

    public NasParamResult(ErrorCode errorCode){
        this.errorCode=errorCode;

    }

    @Override
    public String toString() {
        return "NasParamResult{" +
                "errorCode=" + errorCode +
                ", map=" + map +
                '}';
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public HashMap getMap() {
        return map;
    }

    public void setMap(HashMap map) {
        this.map = map;
    }
}
