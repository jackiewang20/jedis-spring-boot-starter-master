package com.example.redis.crud.starter.component;

import org.springframework.stereotype.Component;

/**
 * @author jackie wang
 * @Title: RedisUtil
 * @ProjectName jedis-spring-boot-starter-master
 * @Description: redis单击版分片模式工具类接口。
 * @date 2019/11/6 11:34
 */
public interface RedisUtil {

    /**
     * 保存到缓存。
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value);

    /**
     * 根据key查询
     * @param key
     * @return
     */
    public String get(String key);

    /**
     * 删除
     * @param key
     * @return
     */
    public Long del(String key);

    /**
     * 设置key的过期时间
     * @param key
     * @param timeout
     * @return
     */
    public Long expire(String key, Integer timeout);

    /**
     * 设置一个值，并设置过期时间
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    public Long set(String key, String value, Integer timeout);

    /**
     * 值递增1
     * @param key
     * @return
     */
    public Long incr(String key);

    /**
     * 是否有key
     * @param key
     * @return
     */
    public Boolean hasKey(String key);

}
