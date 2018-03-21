package com.zm.qconfigdemo.entity;

import java.util.List;

public class UpdateConfigRequest {

    private String topic;
    private List<ConfigEntity> configList;

    public UpdateConfigRequest() {
    }

    public UpdateConfigRequest(String topic, List<ConfigEntity> configList) {
        this.topic = topic;
        this.configList = configList;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<ConfigEntity> getConfigList() {
        return configList;
    }

    public void setConfigList(List<ConfigEntity> configList) {
        this.configList = configList;
    }
}
