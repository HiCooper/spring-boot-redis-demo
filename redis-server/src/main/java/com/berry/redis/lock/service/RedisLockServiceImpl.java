
package com.berry.redis.lock.service;

import com.berry.redis.lock.exception.LockExistsException;
import com.berry.redis.lock.exception.LockNotHeldException;
import com.berry.redis.lock.model.Lock;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Dave Syer
 */
@Service
public class RedisLockServiceImpl implements LockService {

    private static final String DEFAULT_LOCK_PREFIX = "spring.lock.";

    private String prefix = DEFAULT_LOCK_PREFIX;

    /**
     * 30 seconds
     */
    @Setter
    private long expiry = 30000;

    private final RedisOperations<String, String> redisOperations;

    /**
     * The prefix for all lock keys.
     *
     * @param prefix the prefix to set for all lock keys
     */
    public void setPrefix(String prefix) {
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        this.prefix = prefix;
    }

    /**
     * 初始化 redisOperations
     *
     * @param redisConnectionFactory
     */
    public RedisLockServiceImpl(RedisConnectionFactory redisConnectionFactory) {
        Assert.notNull(redisConnectionFactory, "RedisConnectionFactory must not be null");
        this.redisOperations = RedisUtils.stringTemplate(redisConnectionFactory);
    }

    @Override
    public Iterable<Lock> findAll() {
        Set<String> keys = redisOperations.keys(prefix + "*");
        Set<Lock> locks = new LinkedHashSet<Lock>();
        for (String key : keys) {
            Date expires = new Date(System.currentTimeMillis() + redisOperations.getExpire(key, TimeUnit.MILLISECONDS));
            locks.add(new Lock(nameForKey(key), redisOperations.opsForValue().get(key), expires));
        }
        return locks;
    }

    @Override
    public Lock create(String name) {
        String stored = getValue(name);
        if (stored != null) {
            throw new LockExistsException();
        }
        String value = UUID.randomUUID().toString();
        String key = keyForName(name);
        if (!redisOperations.opsForValue().setIfAbsent(key, value)) {
            throw new LockExistsException();
        }
        redisOperations.expire(key, expiry, TimeUnit.MILLISECONDS);
        Date expires = new Date(System.currentTimeMillis() + expiry);
        return new Lock(name, value, expires);
    }

    @Override
    public boolean release(String name, String value) {
        String stored = getValue(name);
        if (stored != null && value.equals(stored)) {
            String key = keyForName(name);
            redisOperations.delete(key);
            return true;
        }
        if (stored != null) {
            throw new LockNotHeldException();
        }
        return false;
    }

    @Override
    public Lock refresh(String name, String value) {
        String key = keyForName(name);
        String stored = getValue(name);
        if (stored != null && value.equals(stored)) {
            Date expires = new Date(System.currentTimeMillis() + expiry);
            redisOperations.expire(key, expiry, TimeUnit.MILLISECONDS);
            return new Lock(name, value, expires);
        }
        throw new LockNotHeldException();
    }

    private String getValue(String name) {
        String key = keyForName(name);
        return redisOperations.opsForValue().get(key);
    }

    private String nameForKey(String key) {
        if (!key.startsWith(prefix)) {
            throw new IllegalStateException("Key (" + key + ") does not start with prefix (" + prefix + ")");
        }
        return key.substring(prefix.length());
    }

    private String keyForName(String name) {
        return prefix + name;
    }

}