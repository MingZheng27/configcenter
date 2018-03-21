package com.zm.qconfigdemo.entity;

public class CreateTopicRequest {

    private String topicName;

    public CreateTopicRequest(String topicName) {
        this.topicName = topicName;
    }

    public CreateTopicRequest() {
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
