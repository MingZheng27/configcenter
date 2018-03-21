package com.zm.qconfigdemo.entity;

public class CreateTopicResponse {

    private boolean isSuccess;
    private Exception ex;

    public CreateTopicResponse(boolean isSuccess, Exception ex) {
        this.isSuccess = isSuccess;
        this.ex = ex;
    }

    public CreateTopicResponse() {
    }

    //responsebody的key是get/set后面的内容
    public boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }
}
