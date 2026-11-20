package com.mcharolabs.cache.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author godfrey.steven
 * @since 2026
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "mcharolabs.cache")
public class CacheProperties {
    /**
     * Key prefix/namespace (like `prefix` in NestJS CacheConfig).
     */
    private String prefix = "";

    /**
     * Default TTL in seconds when ttlSeconds is null on CacheDataDto.
     */
    private long defaultTtlSeconds = 3600;
}