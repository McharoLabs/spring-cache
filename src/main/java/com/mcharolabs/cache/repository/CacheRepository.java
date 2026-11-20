package com.mcharolabs.cache.repository;

import com.mcharolabs.cache.dto.CacheDataDto;
import com.mcharolabs.cache.service.CacheService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author godfrey.steven
 * @since 2026
 */
public abstract class CacheRepository<T extends CacheDataDto, ID> {

    protected final CacheService cacheService;

    private final Class<T> entityType;

    protected CacheRepository(CacheService cacheService, Class<T> entityType) {
        this.cacheService = cacheService;
        this.entityType = entityType;
    }

    protected abstract String getKey(ID id);

    public T save(ID id, T data) {
        return cacheService.set(getKey(id), data);
    }

    public Optional<T> findById(ID id) {
        return cacheService.get(getKey(id), entityType);
    }

    public Optional<T> update(ID id, T data) {
        Optional<T> existingOpt = findById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }

        T existing = existingOpt.get();
        data.setCreatedAt(existing.getCreatedAt());
        data.setUpdatedAt(Instant.now());

        if (data.getTtlSeconds() == null && existing.getTtlSeconds() != null) {
            data.setTtlSeconds(existing.getTtlSeconds());
        }

        return Optional.of(cacheService.set(getKey(id), data));
    }

    public List<Optional<T>> findByIds(List<ID> ids) {
        return ids.stream().map(this::findById).toList();
    }

    public boolean delete(ID id) {
        return cacheService.delete(getKey(id));
    }
}