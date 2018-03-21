package com.zm.qconfigdemo.ConfigProvider;

import com.zm.qconfigdemo.provider.ConfigProvider;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigProviderTest {

    @Test
    public void test(){
        ConfigProvider provider = new ConfigProvider();
        Map<String, String> testMap = new HashMap<>();
        testMap.put("myid", "1");
        testMap.put("myid1", "100");
        provider.writeConfig(testMap, "testTopic01");
    }

    @Test
    public void getDataTest() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        String path = "/config/testTopic";
        Stat stat = new Stat();
        byte[] result = null;
        try {
            result = client.getData().storingStatIn(stat).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObjectInputStream ois = null;
        Map<String, String> map = null; //之前的map
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(result));
            map = (HashMap<String, String>) ois.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
            //log
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //log
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        System.out.println(stat.getVersion());
        client.close();

    }

}
