package com.example.redis.crud.starter.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.function.Function;


/**
 * @author jackie wang
 * @Title: RedisUtilImpl
 * @ProjectName jedis-spring-boot-starter-master
 * @Description: redis单击版分片模式工具类。
 * @date 2019/11/6 14:30
 */
@Component
public class RedisUtilImpl implements RedisUtil {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private ShardedJedisPool shardedJedisPool;

    /**
     * 通用操作方法1：统一处理获取资源，归还资源。（打开链接，关闭链接）
     * @param bizContent 业务描述信息；
     * @param function 函数式接口Function<T, R>，第一个参数T为入参，可以根据入参执行业务操作，第二个参数R为出参；
     * @return
     */
    public <R> R execute(String bizContent, Function<ShardedJedis, R> function) {
        ShardedJedis shardedJedis = null;

        try{
            // 1.从连接池中获取jedis分片对象shardedJedis
            shardedJedis = shardedJedisPool.getResource();

            // 执行业务逻辑
            // 2.shardedJedis执行操作，并返回执行结果
            return function.apply(shardedJedis);
        } catch (Exception e) {
            LOGGER.error("[RedisUtilImpl]Operation redis {} method exception.", bizContent,e);
        } finally {
            // 4.归还资源
            if(null != shardedJedis) {
                shardedJedis.close();
            }
        }

        return null;
    }

    /**
     * 通用操作方法2：统一处理获取资源，归还资源。（打开链接，关闭链接）
     * @param function 函数式接口Function<T, R>，第一个参数T为入参，可以根据入参执行业务操作，第二个参数R为出参；
     * @return
     */
    public <R> R myExecute(MyFunction<ShardedJedis, R> function) {
        ShardedJedis shardedJedis = null;

        try{
            // 从连接池中获取jedis分片对象
            shardedJedis = shardedJedisPool.getResource();

            return function.callback(shardedJedis);
        } catch (Exception e) {
            LOGGER.error("[RedisUtilImpl]Redis operating exception.", e);
        } finally {
            if(null != shardedJedis) {
                shardedJedis.close();
            }
        }

        return null;
    }

    @Override
    public String set(String key, String value) {
        // 3.shardedJedis执行操作
        String returnValue = execute("set(String key, String value)", fun ->{
            return fun.set(key, value);
//            throw new RuntimeException("异常测试");
        });

        return returnValue;
    }

    @Override
    public String get(String key) {
        /** 通用操作方法1 */
//        return execute("get(String key)", fun -> {
//            return fun.get(key);
//        });

        /** 通用操作方法2：需要实现MyFunction接口的callback方法 */
        return myExecute(new MyFunction<ShardedJedis, String>() {
            @Override
            public String callback(ShardedJedis shardedJedis) {
                return shardedJedis.get(key);
            }
        });
    }

    @Override
    public Long del(String key) {
        return execute("del(String key)", fun -> {
            return fun.del(key);
        });
    }

    @Override
    public Long expire(String key, Integer timeout) {
        return execute("expire(String key, Integer timeout)", fun -> {
            return fun.expire(key, timeout);
        });
    }

    @Override
    public Long set(String key, String value, Integer timeout) {
        return execute("set(String key, String value, Integer timeout)",  fun -> {
            fun.set(key, value);
            return fun.expire(key, timeout);
        });
    }

    @Override
    public Long incr(String key) {
        return execute("incr(String key)", fun -> {
            return fun.incr(key);
        });
    }

    @Override
    public Boolean hasKey(String key) {
        return execute("hasKey(String key)", fun -> {
            return fun.exists(key);
        });
    }
}
