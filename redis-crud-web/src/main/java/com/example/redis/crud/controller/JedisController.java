package com.example.redis.crud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.redis.crud.bean.UserVo;
import com.example.redis.crud.starter.component.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

/**
 * @author jackie wang
 * @Title: RedisController
 * @ProjectName redis-spring-boot-starter-master
 * @Description: 使用自定义组件封装后的工具类JedisUtil，暂未实现 TODO
 * redis单击版JedisPool bean初始化。（不推荐）
 * @date 2019/10/30 16:59
 */
@RestController
@RequestMapping("jedis")
public class JedisController {
    @Autowired(required = false)
    private JedisPool jedisPool;

    @RequestMapping(value = "set", method = RequestMethod.GET)
    public String set() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //具体的命令
            jedis.set("JedisPool-hello", "hello jedisPool.");
        } catch (Exception e) {
//            logger.error("op key {} error: " + e.getMessage(), key, e);
            e.printStackTrace();
        } finally {
            //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
            if (jedis != null) {
                jedis.close();
            }
        }
        return "success";
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public String get() {
        String result= null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //具体的命令
            result = jedis.get("JedisPool-hello");
        } catch (Exception e) {
//            logger.error("op key {} error: " + e.getMessage(), key, e);
            e.printStackTrace();
        } finally {
            //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }


}
