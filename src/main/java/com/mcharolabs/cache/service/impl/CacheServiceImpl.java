package com.mcharolabs.cache.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcharolabs.cache.config.CacheProperties;
import com.mcharolabs.cache.dto.CacheDataDto;
import com.mcharolabs.cache.service.CacheService;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * @author godfrey.steven
 * @since 2026
 */
public class CacheServiceImpl implements CacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties properties;

    public CacheServiceImpl(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            CacheProperties properties) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    private String formatKey(String key) {
        String prefix = properties.getPrefix();
        if (prefix == null || prefix.isBlank()) {
            return key;
        }
        return prefix.endsWith(":") ? prefix + key : prefix + ":" + key;
    }

    @Override
    public <T extends CacheDataDto> Optional<T> get(String key, Class<T> type) {
        String json = redisTemplate.opsForValue().get(formatKey(key));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, type));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize cached value for key: " + key, e);
        }
    }

    @Override
    public <T extends CacheDataDto> T set(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            long ttl = value.getTtlSeconds() != null
                    ? value.getTtlSeconds()
                    : properties.getDefaultTtlSeconds();

            String fullKey = formatKey(key);
            if (ttl > 0) {
                redisTemplate.opsForValue().set(fullKey, json, Duration.ofSeconds(ttl));
            } else {
                redisTemplate.opsForValue().set(fullKey, json);
            }
            return value;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize cached value for key: " + key, e);
        }
    }

    @Override
    public boolean delete(String key) {
        Boolean deleted = redisTemplate.delete(formatKey(key));
        return Boolean.TRUE.equals(deleted);
    }

    @Override
    public boolean clear() {
        String prefix = properties.getPrefix();
        String pattern = (prefix == null || prefix.isBlank())
                ? "*"
                : (prefix.endsWith(":") ? prefix + "*" : prefix + ":*");
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return true;
    }
}