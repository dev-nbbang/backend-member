package com.dev.nbbang.member.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    public String getData(String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        return vop.get(key);
    }

    public void setData(String key, String value, long expireTime) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        Duration expire = Duration.ofMillis(expireTime);
        vop.set(key, value, expire);
    }

    public Boolean deleteData(String key) {
        return redisTemplate.delete(key);
    }
}
