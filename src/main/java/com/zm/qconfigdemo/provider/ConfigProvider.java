package com.zm.qconfigdemo.provider;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigProvider {


    public static int version = 0;
    /**
     *
     * @param config 配置文件
     * @return 返回是否写入成功
     */
    public boolean writeConfig(Map<String, String> config,String topic) {
        ByteArrayOutputStream bos = objectToStream(config);
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //fluent形式创建client
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        String path = "/config/" + topic;
        Stat stat = new Stat();
        byte[] result = null;

        try {
            //取会取最新版本
            result = client.getData().storingStatIn(stat).forPath(path);
        } catch (KeeperException.NoNodeException ex) {
            //when there is no node will throw a exception create a node and write log
            //log
            try {
                client.create()
                        .creatingParentsIfNeeded()
                        //.withMode(CreateMode.EPHEMERAL) 临时节点
                        .forPath(path, bos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
                //log
                client.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //log
            client.close();
            return false;
        }

        //存在节点直接写数据
        try {
            client.setData().forPath(path, bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            //log
            client.close();
            return false;
        }
//        ObjectInputStream ois = null;
//        Map<String, String> map = null; //之前的map
//        try {
//            ois = new ObjectInputStream(new ByteArrayInputStream(result));
//            map = (HashMap<String, String>) ois.readObject();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            //log
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            //log
//        }
        //其实每次浏览的时候都会去zk里面拉取最新的数据，添加到config中，不需要判断是否存在了
        // ，直接写(要么使用新节点，要么覆盖原数据(new version))即可
        client.close();
        return true;
    }

    public ByteArrayOutputStream objectToStream(Object object) {
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(bos);
            stream.writeObject(object);
        } catch (Exception ex) {
            ex.printStackTrace();
            //log
            return null;
        }
        return bos;
    }

    public boolean createTopic(String topicName){
        ByteArrayOutputStream bos = objectToStream(new HashMap<String, String>());
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .forPath("/config/" + topicName,bos.toByteArray());
            client.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            //log
            client.close();
            return false;
        }
    }

    public boolean deleteTopic(String topicName){
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        try {
            client.delete()
                    .deletingChildrenIfNeeded()
                    .forPath("/config/" + topicName);
            client.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            //log
            client.close();
            return false;
        }
    }

}
