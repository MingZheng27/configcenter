package com.zm.qconfigdemo.ConfigConsumer;

import com.zm.qconfigdemo.consumer.ConfigConsumer;
import com.zm.qconfigdemo.consumer.ConfigListener;
import org.junit.Test;

import java.util.Map;

public class ConfigConsumerTest {

    @Test
    public void consumerTest() throws Exception {
        ConfigConsumer consumer = new ConfigConsumer("topics");
        consumer.addListener(new ConfigListener() {
            @Override
            public void change(Map<String, String> config) {
                for (Map.Entry<String, String> entry : config.entrySet()) {
                    System.out.println(entry.getKey() + ":" + entry.getValue());
                }
            }
        });
        //异步回调，因此主线程睡眠是没关系的，
        // 这也是为了保持程序一直在跑，到时候SpringBoot跑起来就没事了
        Thread.sleep(Integer.MAX_VALUE);
    }

}
