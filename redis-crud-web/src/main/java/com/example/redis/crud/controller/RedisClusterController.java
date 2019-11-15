package com.example.redis.crud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.redis.crud.bean.UserVo;
import com.example.redis.crud.starter.component.RedisClusterUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * @author jackie wang
 * @Title: RedisClusterController
 * @ProjectName redis-spring-boot-starter-master
 * @Description: 使用自定义组件封装后的工具类RedisUtil
 * @date 2019/10/30 16:59
 */
@RestController
@RequestMapping("redis")
public class RedisClusterController {
    @Autowired(required = false)
    private RedisClusterUtil redisClusterUtil;

    @RequestMapping(value = "set", method = RequestMethod.GET)
    public String set() {
        redisClusterUtil.set("redis-hello", "hello redis.");
        return "success";
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public String get() {
        return redisClusterUtil.get("redis-hello");
    }

    @RequestMapping(value = "hmset", method = RequestMethod.GET)
    public String hmset() {
        UserVo userVo = new UserVo();
        ObjectMapper mapper = new ObjectMapper();
        userVo.setId(1);
        userVo.setName("张三");
        userVo.setDate(new Date());
//        Map<String, String> userVoMap = mapper.convertValue(userVo, Map.class); // jackson默认不支持转换的Map 属性value都是String类型，支持Object类型。

//        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
//        Map<String, Object> userVoMap = (Map<String, Object>) JSON.parseObject(JSON.toJSONString(userVo, SerializerFeature.WriteDateUseDateFormat));

        /**
         * *********示例使用，生产环境不推荐该转换方式*********
         * 虽然支持对象属性转换为Map<String, String>,但是POJO对象所有的属性类型值都变成了String类型，
         * 比如POJO中id，age，date都变成String类型，改变了原有的属性类型 */
        Map<String, String> userVoMap = JSONObject.parseObject(JSON.toJSONString(userVo), new TypeReference<Map<String, String>>() {
        });
//        Map<String, String> userVoMap = (Map<String, String>) JSON.toJSON(userVo);
        redisClusterUtil.hmset("zhangsan", userVoMap);
        return "success";
    }

    @RequestMapping(value = "hmget", method = RequestMethod.GET)
    public String hmget() throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonString = mapper.writeValueAsString(redisClusterUtil.hgetAll("zhangsan"));

        /** redisClusterUtil.hgetAll("key"),返回的类型是Map<String,Sring>,原始数据的id，date属性值都转换了String，改变了原有的属性类型 */
        String jsonString = JSON.toJSONString(redisClusterUtil.hgetAll("zhangsan"));
        return jsonString;
    }

}
