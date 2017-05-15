package com.company.sample.exchange.controller;

/**
 * Placeholder for json error messages.
 */
public class CurrExJsonErrorBody {

    private String status;

    private String errorMsg;

    private String exceptionType;

    private String rawExceptionMsg;

    public CurrExJsonErrorBody(String status, String errorMsg, String exceptionType, String rawExceptionMsg) {
        this.status = status;
        this.errorMsg = errorMsg;
        this.exceptionType = exceptionType;
        this.rawExceptionMsg = rawExceptionMsg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getRawExceptionMsg() {
        return rawExceptionMsg;
    }

    public void setRawExceptionMsg(String rawExceptionMsg) {
        this.rawExceptionMsg = rawExceptionMsg;
    }

}
