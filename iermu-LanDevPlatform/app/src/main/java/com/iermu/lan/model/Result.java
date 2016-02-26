package com.iermu.lan.model;

/**
 * Created by zsj on 15/10/15.
 */
public class Result {


    private ErrorCode errorCode;
    private boolean resultBooean;//返回的boolean
    private String resultString;//返回String
    private int resultInt;//返回Int

    public Result(ErrorCode errorCode){
        this.errorCode=errorCode;

    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isResultBooean() {
        return resultBooean;
    }

    public void setResultBooean(boolean resultBooean) {
        this.resultBooean = resultBooean;
    }

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public int getResultInt() {
        return resultInt;
    }

    public void setResultInt(int resultInt) {
        this.resultInt = resultInt;
    }

    @Override
    public String toString() {
        return "Result{" +
                "errorCode=" + errorCode +
                ", resultBooean=" + resultBooean +
                ", resultString='" + resultString + '\'' +
                ", resultInt=" + resultInt +
                '}';
    }
}
