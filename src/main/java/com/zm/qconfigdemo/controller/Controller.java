package com.zm.qconfigdemo.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sun.istack.internal.Nullable;
import com.zm.qconfigdemo.entity.*;
import com.zm.qconfigdemo.service.ConfigService;
import com.zm.qconfigdemo.util.CommonFunc;
import org.apache.zookeeper.KeeperException;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {

    @Resource(name = "configService")
    ConfigService configService;

    @ResponseBody
    @RequestMapping(value = "/getConfig/{topic}",method = RequestMethod.GET)
    public List<ConfigEntity> getConfig(@PathVariable String topic){
        try {
            return configService.getConfig(topic);
        } catch (KeeperException.NoNodeException ex) {
            List<ConfigEntity> errorList = Lists.newArrayList();
            errorList.add(new ConfigEntity("unkown appid", "unkown appid"));
            return errorList;
        } catch (Exception e) {
            e.printStackTrace();
            //log
            List<ConfigEntity> errorList = Lists.newArrayList();
            ConfigEntity errorEntity = new ConfigEntity("error", "error");
            errorList.add(errorEntity);
            return errorList;
        }
    }

    //获取app列表
    @ResponseBody
    @RequestMapping(value = "/getTopics",method = RequestMethod.GET)
    public List<String> getTopics(){
        try {
            return configService.getTopics();
        } catch (Exception e) {
            e.printStackTrace();
            //log
            List<String> errorList = Lists.newArrayList();
            errorList.add("getTopics error");
            return errorList;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/updateConfig",method = RequestMethod.POST)
    public UpdateConfigResponse updateConfig(HttpServletRequest request) {
//        BufferedReader bfr;
        String inputJsonString;
//        try {
//            bfr = new BufferedReader(new InputStreamReader(request.getInputStream()));
//            String line;
//            StringBuilder sb = new StringBuilder();
//            while ((line = bfr.readLine()) != null) {
//                sb.append(line);
//            }
//            inputJsonString = sb.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new UpdateConfigResponse(false, e);
//        }
        try {
            inputJsonString = CommonFunc.inputStream2String(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            //log
            return new UpdateConfigResponse(false, e);
        }
        if (!StringUtils.isEmpty(inputJsonString)) {
            //fastJSON转换后丢给Service层写数据
            UpdateConfigRequest req = JSON.parseObject(inputJsonString, UpdateConfigRequest.class);
            if (configService.updateConfig(req)){
                return new UpdateConfigResponse(true);
            } else {
                return new UpdateConfigResponse(false);
            }
        } else {
            return new UpdateConfigResponse(false, new Exception("request is empty"));
        }

    }

    //todo:test
    @ResponseBody
    @RequestMapping(value = "/createTopic")
    public CreateTopicResponse createTopic(HttpServletRequest request){
//        StringBuilder sb = new StringBuilder();
//        try {
//            BufferedReader bfr = new BufferedReader(new InputStreamReader(request.getInputStream()));
//            String line;
//            while ((line = bfr.readLine()) != null) {
//                sb.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            //log
//            return new CreateTopicResponse(false, e);
//        }
        String inputJsonString;
        try {
            inputJsonString = CommonFunc.inputStream2String(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            //log
            return new CreateTopicResponse(false, e);
        }
        if (!StringUtils.isEmpty(inputJsonString)) {
            CreateTopicRequest req = JSON.parseObject(inputJsonString, CreateTopicRequest.class);
            if (CommonFunc.isAvaliableAppid(req.getTopicName()) && configService.createTopics(req.getTopicName())) {
                return new CreateTopicResponse(true, null);
            } else {
                return new CreateTopicResponse(false, new Exception("create topic failed"));
            }
        } else {
            return new CreateTopicResponse(false, new Exception("request is null"));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteTopic")
    public DeleteTopicResponse deleteTopic(HttpServletRequest request) {
        String inputJsonString;
        try {
            inputJsonString = CommonFunc.inputStream2String(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            //log
            return new DeleteTopicResponse(false, e);
        }
        if (!StringUtils.isEmpty(inputJsonString)) {
            DeleteTopicRequest req = JSON.parseObject(inputJsonString, DeleteTopicRequest.class);
            if (configService.deleteTopic(req.getTopicName())) {
                return new DeleteTopicResponse(true, null);
            } else {
                return new DeleteTopicResponse(false, new Exception("delete topic failed"));
            }
        } else {
            return new DeleteTopicResponse(false, new Exception("request is null"));
        }
    }

    @RequestMapping(value = "/config.html")
    public String index(Model model,HttpServletRequest request){
        //ModelAndView要自己建，而Model会自带,只是使用方式不一样，其实是一样的
        try {
            model.addAttribute("appids",configService.getTopics());
            String topicId = (String) request.getParameter("topicId");
            if (!StringUtils.isEmpty(topicId)) {
                model.addAttribute("configs", configService.getConfig(topicId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //log
            return "error";
        }
        return "index";
    }

    @RequestMapping(value = "/deleteapp.html")
    public String forwardDeletePage(Model model) {
        try {
            List<String> topics = configService.getTopics();
            model.addAttribute("appids", topics);
        } catch (Exception e) {
            e.printStackTrace();
            //log
            return "error";
        }
        return "deleteapp";
    }

    @RequestMapping(value = "/createapp.html")
    public String forwardCreatePage(Model model){
        try {
            List<String> topics = configService.getTopics();
            model.addAttribute("appids", topics);
        } catch (Exception e) {
            e.printStackTrace();
            //log
            return "error";
        }
        return "createapp";
    }

}
