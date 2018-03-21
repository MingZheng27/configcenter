package com.zm.qconfigdemo.service;

import com.google.common.collect.Lists;
import com.zm.qconfigdemo.entity.ConfigEntity;
import com.zm.qconfigdemo.entity.UpdateConfigRequest;
import com.zm.qconfigdemo.provider.ConfigProvider;
import com.zm.qconfigdemo.util.CommonFunc;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("configService")
public class ConfigService {

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    private CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectionTimeoutMs(3000)
            .sessionTimeoutMs(5000)
            .retryPolicy(retryPolicy)
            .connectString("127.0.0.1:2181")
            .build();
    private Stat stat = new Stat();

    public ConfigService() {
        client.start(); //因为单例的原因只能被start一次
    }

    public List<ConfigEntity> getConfig(String topic) throws Exception {
        byte[] result = client.getData().storingStatIn(stat).forPath("/config/" + topic);
        ByteArrayInputStream bis = new ByteArrayInputStream(result);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Map<String,String> resultMap = (Map<String, String>) ois.readObject();
        List<ConfigEntity> resultList = Lists.newArrayList();
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            ConfigEntity entity = new ConfigEntity(entry.getKey(), entry.getValue());
            resultList.add(entity);
        }
        return resultList;
    }

    public List<String> getTopics() throws Exception {
        List<String> resultList = client.getChildren().forPath("/config");
        resultList.removeIf(s -> !CommonFunc.isAvaliableAppid(s));
        return resultList;
    }

    public boolean updateConfig(UpdateConfigRequest request){
        ConfigProvider provider = new ConfigProvider();
        Map<String, String> configMap = new HashMap<>();
        for (ConfigEntity entity : request.getConfigList()) {
            configMap.put(entity.getKey(), entity.getValue());
        }
        return provider.writeConfig(configMap, request.getTopic());
    }

    public boolean createTopics(String topicName){
        ConfigProvider provider = new ConfigProvider();
        return provider.createTopic(topicName);
    }

    public boolean deleteTopic(String topicName){
        ConfigProvider provider = new ConfigProvider();
        return provider.deleteTopic(topicName);
    }

}
