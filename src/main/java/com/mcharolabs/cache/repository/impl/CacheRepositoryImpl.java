package com.mcharolabs.cache.repository.impl;

import com.mcharolabs.cache.dto.CacheDataDto;
import com.mcharolabs.cache.repository.CacheRepository;
import com.mcharolabs.cache.service.CacheService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * @author godfrey.steven
 * @since 2026
 */
@RequiredArgsConstructor
public class CacheRepositoryImpl<ID, T extends CacheDataDto> implements CacheRepository<ID, T> {

    private final CacheService cacheService;
    private final Class<T> valueType;

    @Override
    public T save(ID id, T value) {
        return cacheService.set(String.valueOf(id), value);
    }

    @Override
    public Optional<T> findById(ID id) {
        return cacheService.get(String.valueOf(id), valueType);
    }

    @Override
    public boolean deleteById(ID id) {
        return cacheService.delete(String.valueOf(id));
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}