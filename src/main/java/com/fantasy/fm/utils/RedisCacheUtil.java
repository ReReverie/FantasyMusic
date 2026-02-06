package com.fantasy.fm.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisCacheUtil {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 存储对象
     */
    public <T> void set(String key, T value) {
        String json = objectMapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key, json);
    }

    /**
     * 存储对象并设置过期时间
     */
    public <T> void set(String key, T value, Long timeout, TimeUnit unit) {
        String json = objectMapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key, json, timeout, unit);
    }

    /**
     * 获取对象
     */
    public <T> T get(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, clazz);
    }

    /**
     * 获取对象（支持泛型，如 List<MusicListVO>）
     */
    public <T> T get(String key, TypeReference<T> typeReference) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, typeReference);
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断缓存是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.opsForValue().get(key) != null;
    }

    /**
     * 如果不存在则存储值,如果存在则进行自增
     */
    public long increment(String ipKey, int i) {
        return redisTemplate.opsForValue().increment(ipKey, i);
    }

    /**
     * 设置过期时间
     */
    public void expire(String ipKey, long l, TimeUnit timeUnit) {
        redisTemplate.expire(ipKey, l, timeUnit);
    }
}
