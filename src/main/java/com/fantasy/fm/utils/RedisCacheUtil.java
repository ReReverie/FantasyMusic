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
}
