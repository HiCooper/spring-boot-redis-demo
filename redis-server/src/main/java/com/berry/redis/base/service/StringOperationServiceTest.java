package com.berry.redis.base.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Berry_Cooper
 * @date 2017/12/7.
 */
@Service
public class StringOperationServiceTest {

    /**
     * 使用String数据结构操作，建议使用stringTemplate，否则在操作自增时会出错
     */
    @Resource
    private StringRedisTemplate stringTemplate;

    /**
     * 设值
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        stringTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return stringTemplate.opsForValue().get(key);
    }

    public void setWithExpire(String key, String value, Long timeout, TimeUnit timeUnit) {
        stringTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public Boolean setIfAbsent(String key, String value) {
        return stringTemplate.opsForValue().setIfAbsent(key, value);
    }

    public Long increment(String key, Long value) {
        return stringTemplate.opsForValue().increment(key, value);
    }

    public Long getExpire(String key) {
        return stringTemplate.opsForValue().getOperations().getExpire(key);
    }


}
