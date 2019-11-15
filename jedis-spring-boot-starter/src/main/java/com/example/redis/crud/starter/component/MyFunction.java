package com.example.redis.crud.starter.component;

/**
 * @author jackie wang
 * @Title: MyFunction
 * @ProjectName jedis-spring-boot-starter-master
 * @Description: 自定义接口，回调方法。
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @date 2019/11/6 16:17
 */
public interface MyFunction<T, R> {

    public R callback(T t);

}
