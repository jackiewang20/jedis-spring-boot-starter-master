readme

0.概述
    当前示例的自定义starter组件redis-spring-boot-starter，默认使用redis客户端库lettuce。

    使用spring boot整合redis，客户端组件通常使用jedis和lettuce，jedis存在一些BUG，生产环境中遇到问题，lettuce兼容性较好推荐
使用lettuce。
    spring boot 1.5.x版本使用RedisTemplate类默认依赖jedis，spring boot 2.x版本默认引用lettuce，如果在spring boot 1.5.x版本
使用lettuce组件包，需要自定义配置属性和配置注解类。

如果使用jedis客户端：
1.maven配置如下：
    <dependencies>
        <!-- spring boot redis数据操作：默认依赖jedis客户端包 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
<!--            <exclusions>
                <exclusion>
                    <artifactId>jedis</artifactId>
                    <groupId>redis.clients</groupId>
                </exclusion>
            </exclusions>-->
        </dependency>
        <!-- redis操作：lettuce客户端包 -->
<!--        <dependency>
            <groupId>biz.paluch.redis</groupId>
            <artifactId>lettuce</artifactId>
            <version>4.4.6.Final</version>
        </dependency>-->
        <!-- 数据库连接池 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
        <!-- jackson json序列化 -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
 </dependencies>

2.激活JedisAutoConfiguration.java，注释RedisAutoConfiguration.java


1.环境
1.1.开发环境
    Spring boot 版本：1.5.x
    Lettuce版本：4.4.6

1.2.版本冲突问题
    spring cloud 对应spring boot 2.x版本：
        spring cloud版本使用<spring-cloud.version>Greenwich.SR3</spring-cloud.version>
        spring boot版本使用<version>1.5.20.RELEASE</version>

    spring cloud 对应spring boot 1.5.x版本：
        spring cloud版本使用<spring-cloud.version>Edgware.SR5</spring-cloud.version>
        spring boot版本使用<version>2.1.9.RELEASE</version>

    说明：spring cloud版本和spring boot版本要对应，否则启动包冲突错误：
    ERROR org.springframework.boot.SpringApplication - Application startup failed
    java.lang.NoSuchMethodError: org.springframework.boot.builder.SpringApplicationBuilder.<init>([Ljava/lang/Class;)V


2.maven依赖

        <!-- spring boot redis数据操作：排除默认依赖的jedis客户端包 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <exclusions>
                <exclusion>
                    <artifactId>jedis</artifactId>
                    <groupId>redis.clients</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <!-- redis操作：lettuce客户端包 -->
        <dependency>
            <groupId>biz.paluch.redis</groupId>
            <artifactId>lettuce</artifactId>
            <version>4.4.6.Final</version>
        </dependency>
        <!-- 数据库连接池 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
        <!-- jackson json序列化 -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

3.自定义属性
    由于spring-boot-autoconfigure.x.jar包包含redis属性类RedisProperties.java，可以直接使用。不允许继承，因为
RedisProperties中RedisProperties.Lettuce属性引用了RedisProperties类，子类中的RedisProperties.Lettuce属性也
继承了，因此子类bean初始化的时候会再次创建RedisProperties bean，会同时创建多个相同的bean。

4.自定义redis Bean配置
    RedisAutoConfiguration.java

5.Controller使用

6.测试
6.1.测试集群版

6.2.测试单机版


