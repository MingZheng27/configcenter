package com.zm.qconfigdemo.entity;

public class DeleteTopicResponse {

    private boolean isSuccess;
    private Exception ex;

    public DeleteTopicResponse(boolean isSuccess, Exception ex) {
        this.isSuccess = isSuccess;
        this.ex = ex;
    }

    public DeleteTopicResponse() {
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }
}
