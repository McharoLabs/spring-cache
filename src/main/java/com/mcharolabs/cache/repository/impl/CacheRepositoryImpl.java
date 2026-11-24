package com.mcharolabs.cache.repository.impl;

import com.mcharolabs.cache.dto.CacheDataDto;
import com.mcharolabs.cache.repository.CacheRepository;
import com.mcharolabs.cache.service.CacheService;
import java.util.Objects;
import java.util.Optional;

/**
 * Default {@link CacheRepository} implementation backed by {@link CacheService}.
 *
 * @author godfrey.steven
 * @since 2026
 */
public abstract class CacheRepositoryImpl<ID, T extends CacheDataDto> extends CacheRepository<ID, T> {

    protected final CacheService cacheService;
    private final Class<T> valueType;

    protected CacheRepositoryImpl(CacheService cacheService, Class<T> valueType) {
        this.cacheService = Objects.requireNonNull(cacheService, "cacheService must not be null");
        this.valueType = Objects.requireNonNull(valueType, "valueType must not be null");
    }

    @Override
    public T save(ID id, T value) {
        return cacheService.set(getKey(id), value);
    }

    @Override
    public Optional<T> findById(ID id) {
        return cacheService.get(getKey(id), valueType);
    }

    @Override
    public boolean deleteById(ID id) {
        return cacheService.delete(getKey(id));
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}