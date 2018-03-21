package com.zm.qconfigdemo.service;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import java.util.List;

public class ServiceTest {

    @Test
    public void getTopicsTest() throws Exception {
        ConfigService service = new ConfigService();
        List<String> list = service.getTopics();
        for (String s : list) {
            System.out.println(s);
        }
    }

    @Test
    public void createTopicTest(){
        ConfigService service = new ConfigService();
        service.createTopics("testTopics02");
    }

    @Test
    public void getTopicDataTest() throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        byte[] result = client.getData().forPath("/config/testTopics02");
        client.close();
        System.out.println(new String(result));
    }

    @Test
    public void deleteTopicTest(){
        ConfigService service = new ConfigService();
        System.out.println(service.deleteTopic("null"));
    }

}
