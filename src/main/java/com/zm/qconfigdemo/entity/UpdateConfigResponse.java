package com.zm.qconfigdemo.entity;

public class UpdateConfigResponse {

    private boolean isSuccess;
    private Exception exception;

    public UpdateConfigResponse() {
    }

    public UpdateConfigResponse(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public UpdateConfigResponse(boolean isSuccess, Exception exception) {
        this.isSuccess = isSuccess;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
