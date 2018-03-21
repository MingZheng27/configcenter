package com.zm.qconfigdemo.consumer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigConsumer {

    private static ConfigListener listener;
    private CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    private Stat stat;
    private String path;

    public ConfigConsumer(String topic) {
        this.path = "/config/" + topic;
        client.start();
    }

    //注册listener后并且触发获取数据同时给client注册NodeCache
    public void addListener(ConfigListener listener) throws Exception {
        this.listener = listener;
        final Map<String, String> result = new HashMap<>();

        NodeCache cache = new NodeCache(client, path, false);
        cache.start();
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                //即使定义为final也不能直接赋值，final的问题再看！final只能在构造方法里面赋值一次，
                // 只能调用其自身的方法来修改自身内容，因此使用putAll是可以的
                result.putAll(byteArrayToMap(client.getData().storingStatIn(stat).forPath(path)));
                listener.change(result);
            }
        });
        //第一次拉一下(应该是不需要的,应该在添加listener的时候应该就会自动拉一下了)，
        // 后面就是回调拉数据了
        //result.putAll(byteArrayToMap(client.getData().storingStatIn(stat).forPath(path)));
        //listener.change(result);
    }

    public Map<String,String> byteArrayToMap(byte[] result){
        ObjectInputStream ois = null;
        Map<String, String> map = null; //之前的map
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(result));
            map = (HashMap<String, String>) ois.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
            //log
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //log
            return null;
        }
        return map;
    }

}
