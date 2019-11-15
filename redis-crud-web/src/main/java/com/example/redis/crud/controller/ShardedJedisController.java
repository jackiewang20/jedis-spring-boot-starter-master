package com.example.redis.crud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Date;
import java.util.Map;

/**
 * @author jackie wang
 * @Title: RedisController
 * @ProjectName redis-spring-boot-starter-master
 * @Description: 使用自定义组件封装后的工具类RedisUtil
 * @date 2019/10/30 16:59
 */
@RestController
@RequestMapping("sharded/jedis")
public class ShardedJedisController {
    @Autowired
    private ShardedJedisPool shardedJedisPool;

    @Autowired
    private RedisUtil redisUtil;

    /** 原始的实现 */
    @RequestMapping(value = "set", method = RequestMethod.GET)
    public String set() {
        ShardedJedis jedis = shardedJedisPool.getResource();
        try {
            jedis.set("a-jedis-hello", "hello jedis.");
        } finally {
            jedis.close();
        }
        return "success";
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public String get() {
        String result = null;
        ShardedJedis jedis = shardedJedisPool.getResource();
        try{
            result = jedis.get("a-jedis-hello");
        } finally {
            jedis.close();
        }

        return result;
    }

    /** ========== 使用工具类RedisUtil ========= */
    @RequestMapping(value = "set2", method = RequestMethod.GET)
    public String set2() {
        redisUtil.set("ShardedJedisPool", "Class ShardedJedisPool is used.");
        return "success";
    }

    @RequestMapping(value = "get2", method = RequestMethod.GET)
    public String get2() {
        String result = null;
        result = redisUtil.get("ShardedJedisPool");

        return result;
    }

}
