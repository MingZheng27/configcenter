package com.zm.qconfigdemo.consumer;

import java.util.Map;

public interface ConfigListener {

    void change(Map<String,String> config);

}
