package com.mcharolabs.cache.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.SuperBuilder;

import java.time.Instant;

/**
 * @author godfrey.steven
 * @since 2026
 */
@Getter
@Setter
@SuperBuilder
public abstract class CacheDataDto {
    private Long ttlSeconds;
    private Instant createdAt;
    private Instant updatedAt;

    protected CacheDataDto() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    protected CacheDataDto(Long ttlSeconds) {
        this();
        this.ttlSeconds = ttlSeconds;
    }
}
