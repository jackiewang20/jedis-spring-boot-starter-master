package com.example.redis.crud.starter.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jackie wang
 * @Title: RedisUtil
 * @ProjectName jedis-spring-boot-starter-master
 * @Description: RedisUtil工具包，封装JedisCluster bean。
 * @date 2019/10/30 16:59
 */
@Component
public class RedisClusterUtil {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private JedisCluster jedisCluster;

    public Set<String> keys(String keys) {
        try {
            return jedisCluster.hkeys(keys);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return null;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @return
     */
    public boolean expire(String key, int timeout) {
        try {
            if (timeout > 0) {
                jedisCluster.expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键； 不能为null
     * @return 时间(秒)； 返回0代表为永久有效
     */
//    public Long getExpire(String key) {
//        return jedisCluster.getExpire(key, TimeUnit.SECONDS);
//    }

    /**
     * 判断key是否存在
     *
     * @param key 键；
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return jedisCluster.exists(key);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值， 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                jedisCluster.del(key[0]);
            } else {
                jedisCluster.del(key);
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键；
     * @return 值；
     */
    public String get(String key) {
        if (key == null) {
            return null;
        }
        Object result = jedisCluster.get(key);
        if (result != null) {
            return result.toString();
        }
        return null;
    }

    /**
     * 普通缓存放入
     *
     * @param key   键；
     * @param value 值；
     * @return true成功 false失败
     */
    public boolean set(String key, String value) {
        try {
            jedisCluster.set(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key     键；
     * @param value   值；
     * @param timeout 时间(秒)； timeout要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, String value, Long timeout) {
        try {
            if (hasKey(key)) {
                jedisCluster.del(key);
            }

            // NX是不存在时才set， XX是存在时才set， EX是秒，PX是毫秒
            if (timeout > 0) {
                jedisCluster.set(key, value, "NX", "EX", timeout);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键；
     * @param delta 要增加几(大于0)
     * @return
     */
    public Long incr(String key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return jedisCluster.incrBy(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键；
     * @param delta 要减少几(小于0)
     * @return
     */
    public Long decr(String key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return jedisCluster.incrBy(key, -delta);
    }

    /**
     * 获取Hash项的内容。
     *
     * @param key  键； 不能为null
     * @param item 项 不能为null
     * @return 值；
     */
    public String hget(String key, String item) {
        return jedisCluster.hget(key, item);
    }

    /**
     * 获取hashKey对应的键的值；
     *
     * @param key 键；
     * @return 对应的多个属性；
     */
    public List<String> hmget(String key, String[] fields) {
        return jedisCluster.hmget(key, fields);
    }

    /**
     * 获取hashKey对应的键的值；
     *
     * @param key 键；
     * @return 对应的多个属性值；
     */
    public Map<String, String> hgetAll(String key) {
        return jedisCluster.hgetAll(key);
    }

    /**
     * HashSet
     *
     * @param key 键；
     * @param map 对应多个键值；
     * @return
     */
    public String hmset(String key, Map<String, String> map) {
        return jedisCluster.hmset(key, map);
    }


    /**
     * HashSet 并设置时间
     *
     * @param key     键；
     * @param map     对应多个键值；
     * @param timeout 时间(秒)；
     * @return
     */
    public String hmset(String key, Map<String, String> map, int timeout) {
        String result = null;
        try {
            result = jedisCluster.hmset(key, map);
            if (timeout > 0) {
                expire(key, timeout);
            }
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
        }
        return result;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键；
     * @param item  项
     * @param value 值；
     * @return true 成功 false失败
     */
    public Long hset(String key, String item, String value) {
        return jedisCluster.hset(key, item, value);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键；
     * @param item    项
     * @param value   值；
     * @param timeout 时间(秒)； 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public Long hset(String key, String item, String value, int timeout) {
        Long result = null;
        try {
            result = jedisCluster.hset(key, item, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
        }
        return result;
    }

    /**
     * 删除hash表中的值；
     *
     * @param key  键； 不能为null
     * @param item 项 可以使多个,不能为null
     */
    public void hdel(String key, String... item) {
        jedisCluster.hdel(key, item);
    }

    /**
     * 判断hash表中是否有该项的值；
     *
     * @param key  键； 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return jedisCluster.hexists(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值；返回
     *
     * @param key  键；
     * @param item 项
     * @param by   要增加
     * @return
     */
    public Long hincr(String key, String item, Long by) {
        if (by == null || by <= 0) {
            throw new IllegalArgumentException("The parameter 'by' must be greater than 0.");
        }
        return jedisCluster.hincrBy(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键；
     * @param item 项
     * @param by   要减少
     * @return
     */
    public Long hdecr(String key, String item, Long by) {
        if (by == null || by <= 0) {
            throw new IllegalArgumentException("The parameter 'by' must be greater than 0.");
        }
        return jedisCluster.hincrBy(key, item, -by);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key          键；
     * @param scoreMembers 值，可以是多个
     * @return 成功个数
     */
    public Long sSet(String key, Map<String, Double> scoreMembers) {
        try {
            return jedisCluster.zadd(key, scoreMembers);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return 0L;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键；
     * @param score  分数；
     * @param member 值
     * @return 成功个数
     */
    public Long sSet(String key, Double score, String member) {
        try {
            return jedisCluster.zadd(key, score, member);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return 0L;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key          键；
     * @param scoreMembers 值，可以是多个
     * @param timeout      时间(秒)；
     * @return 成功个数
     */
    public Long sSetAndTime(String key, Map<String, Double> scoreMembers, Integer timeout) {
        try {
            Long count = jedisCluster.zadd(key, scoreMembers);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return count;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return 0L;
        }
    }

    /**
     * 移除值；为value的
     *
     * @param key    键；
     * @param values 值，可以是多个
     * @return 移除的个数
     */
    public Long setRemove(String key, String... values) {
        try {
            Long count = jedisCluster.zrem(key, values);
            return count;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return 0L;
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键；
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值；
     * @return
     */
    public List<String> lGet(String key, Long start, Long end) {
        try {
            return jedisCluster.lrange(key, start, end);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键；
     * @return
     */
    public Long lGetListSize(String key) {
        try {
            return jedisCluster.llen(key);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return 0L;
        }
    }

    /**
     * 通过索引获取list中的值；。
     *
     * @param key   键；
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, Long index) {
        try {
            return jedisCluster.lindex(key, index);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return null;
        }
    }

    /**
     * 将list放入缓存，在列表尾部添加一个值，。
     *
     * @param key   键；
     * @param value 值；
     * @return
     */
    public boolean rPush(String key, String... value) {
        try {
            jedisCluster.rpush(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，在列表尾部添加一个值，。
     *
     * @param timeout 时间(秒)；
     * @param key     键；
     * @param value   值；
     * @return
     */
    public boolean rPush(Integer timeout, String key, String... value) {
        try {
            jedisCluster.rpush(key, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，将一个值，插入到列表头部。
     *
     * @param key   键；
     * @param value 值；
     * @return
     */
    public boolean lPush(String key, String... value) {
        try {
            jedisCluster.lpush(key, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 将list放入缓存，将一个值，插入到列表头部。
     *
     * @param timeout 时间(秒)；
     * @param key     键；
     * @param value   值；
     * @return
     */
    public boolean lPush(Integer timeout, String key, String... value) {
        try {
            jedisCluster.lpush(key, value);
            if (timeout > 0) {
                expire(key, timeout);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据。
     *
     * @param key   键；
     * @param index 索引
     * @param value 值；
     * @return
     */
    public boolean lUpdateIndex(String key, Long index, String value) {
        try {
            jedisCluster.lset(key, index, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return false;
        }
    }

    /**
     * 移除N个值，为value。
     *
     * @param key   键；
     * @param count 移除多少个；
     * @param value 值；
     * @return 移除的个数
     */
    public Long lRemove(String key, Long count, String value) {
        try {
            Long remove = jedisCluster.lrem(key, count, value);
            return remove;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return 0L;
        }
    }

    /**
     * 移出并获取列表的第一个元素。
     *
     * @param key 键；
     * @return
     */
    public String lPop(String key) {
        try {
            return jedisCluster.lpop(key);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return null;
        }
    }

    /**
     * 移出并获取列表的第一个元素。
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @return
     */
    public String lPop(String key, Integer timeout) {
        String result = null;
        try {
            result = jedisCluster.lpop(key);
            expire(key, timeout);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
        }
        return result;
    }

    /**
     * 移除列表的最后一个元素，返回值为移除的元素。
     *
     * @param key 键；
     * @return
     */
    public String rPop(String key) {
        try {
            return jedisCluster.rpop(key);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return null;
        }
    }

    /**
     * 移除列表的最后一个元素，返回值为移除的元素。
     *
     * @param key     键；
     * @param timeout 时间(秒)；
     * @return
     */
    public String rPop(String key, Integer timeout) {
        String result = null;
        try {
            result = jedisCluster.rpop(key);
            expire(key, timeout);
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
        }
        return result;
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param timeout 时间(秒)；
     * @param keys    键；
     * @return
     */
    public List<String> bLPop(Integer timeout, String... keys) {
        try {
            List<String> list = jedisCluster.blpop(timeout, keys);
            return list;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return null;
        }
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @param timeout 时间(秒)；
     * @param keys    键；
     * @return
     */
    public List<String> bRPop(Integer timeout, String... keys) {
        try {
            List<String> list = jedisCluster.brpop(timeout, keys);
            return list;
        } catch (Exception e) {
            LOGGER.error("[RedisClusterUtil]Redis operating exception.", e);
            return null;
        }
    }

}

