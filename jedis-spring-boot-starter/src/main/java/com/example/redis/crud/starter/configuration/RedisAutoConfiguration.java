package com.example.redis.crud.starter.configuration;

import com.sun.jndi.toolkit.url.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jackie wang
 * @Title: RedisAutoConfiguration
 * @ProjectName redis-spring-boot-starter-master
 * @Description: Redis客户端自动配置，默认使用jedis客户端库。
 * @date 2019/10/30 11:36
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class) // 激活配置属性类
public class RedisAutoConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    /**
     * redis集群版bean初始化。
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(value = "spring.redis.cluster.nodes") // 条件加载配置属性，如果满足条件，加载当前bean
    public JedisCluster jedisCluster() {
        List<String> nodes = redisProperties.getCluster().getNodes();
        Set<HostAndPort> hostAndPortSet = new HashSet<>();
        for (String node : nodes) {
//            String[] hostAndPort = node.split(":");
//            hostAndPortSet.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
            hostAndPortSet.add(getHostAndPort(node));
        }

        JedisCluster jedisCluster = null;
        if (StringUtils.hasText(redisProperties.getPassword())) {
            jedisCluster = new JedisCluster(hostAndPortSet, redisProperties.getTimeout(), redisProperties.getTimeout(),
                    3, redisProperties.getPassword(), getPoolConfig());
        } else {
            jedisCluster = new JedisCluster(hostAndPortSet, redisProperties.getTimeout(), redisProperties.getTimeout(),
                    3, getPoolConfig());
        }
        return jedisCluster;
    }

    /**
     * redis单击版JedisPool bean初始化。（不推荐）
     * 使用示例：
     * Jedis jedis = null;
     * try {
     * jedis = jedisPool.getResource();
     * //具体的命令
     * jedis.executeCommand()
     * } catch (Exception e) {
     * logger.error("op key {} error: " + e.getMessage(), key, e);
     * } finally {
     * //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
     * if (jedis != null)
     * jedis.close();
     * }
     * 说明：实际使用需要封装。
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(value = "spring.redis.host")
    @ConditionalOnMissingBean(JedisCluster.class)  // 如果集群模式JedisCluster bean创建，则不执行
    public JedisPool jedisPool() {
        String host = redisProperties.getHost();
        Integer port = redisProperties.getPort();
        Integer timeout = null;
        if (redisProperties.getTimeout() > 0) {
            timeout = redisProperties.getTimeout();
        } else {
            timeout = 30000;
        }
        String password = redisProperties.getPassword();
        Integer database = redisProperties.getDatabase();

        return new JedisPool(getPoolConfig(), host, port, timeout, password, database);
    }

    /**
     * 连接池配置：redis单击版分片模式ShardedJedisPool bean初始化。在redis 2.x版本单机版中支持水平分片扩展节点，
     * 通过hash算法实现多节点操作。
     * 使用示例：
     * ShardedJedis jedis = shardedJedisPool.getResource();
     *   try {
     *     jedis.set("hello", "jedis");
     *   } finally {
     *     jedis.close();
     *   }
     *
     * 使用示例更多参考：https://www.programcreek.com/java-api-examples/index.php?api=redis.clients.jedis.JedisShardInfo
     * @return
     */
    @Bean
    @ConditionalOnProperty(value = "spring.redis.host")
    @ConditionalOnMissingBean(JedisCluster.class)  // 如果集群模式JedisCluster bean创建，则不执行
    public ShardedJedisPool shardedJedisPool() throws Exception {
        List<JedisShardInfo> shards = new ArrayList<>();
        /** 添加节点1的主机信息 */
        JedisShardInfo jedisShardInfo = new JedisShardInfo(redisProperties.getHost(), redisProperties.getPort());
        // 设置数据库
        setDatabase(jedisShardInfo, redisProperties.getDatabase());
        shards.add(jedisShardInfo);
        if(StringUtils.hasText(redisProperties.getPassword())) {
            shards.get(0).setPassword(redisProperties.getPassword());
        }

        // 添加节点n
//        shards.add(new JedisShardInfo(redisProperties.getHost(), redisProperties.getPort()));
//        shards.get(n).setPassword(redisProperties.getPassword());




        return new ShardedJedisPool(getPoolConfig(), shards);
    }

    private void setDatabase(JedisShardInfo jedisShardInfo, Integer database) {
        Class<? extends JedisShardInfo> clz = jedisShardInfo.getClass();
        Field declaredField = null;
        try {
            declaredField = clz.getDeclaredField("db");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("JedisShardInfo no field 'db'.");
        }
        declaredField.setAccessible(true);
        try {
            declaredField.set(jedisShardInfo, database);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Illegally set JedisShardInfo's property 'db'.");
        }
    }

    /**
     * 获取redis主机地址和端口号。
     * @param hostAndPort
     * @return
     */
    private HostAndPort getHostAndPort(String hostAndPort) {
        String[] myHostAndPort = hostAndPort.split(":");
        Assert.notNull(myHostAndPort, "HostAndPort need to be seperated by  ':'.");
        Assert.isTrue(myHostAndPort.length == 2, "Invalid host address, for example: '192.168.10.1:7000'");

        return new HostAndPort(myHostAndPort[0], Integer.valueOf(myHostAndPort[1]));
    }

    /**
     * 连接池配置
     */
    @Bean
    public JedisPoolConfig getPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
        poolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());
        poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        return poolConfig;
    }

}
