package com.zm.qconfigdemo.entity;

public class DeleteTopicRequest {

    private String topicName;

    public DeleteTopicRequest() {
    }

    public DeleteTopicRequest(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
