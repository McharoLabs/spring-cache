package com.mcharolabs.cache.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author godfrey.steven
 * @since 2026
 */
@Getter
@Setter
@SuperBuilder
public abstract class CacheDataDto {

    private Long ttlSeconds;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();

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