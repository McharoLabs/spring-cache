package com.mcharolabs.cache.repository;

import com.mcharolabs.cache.dto.CacheDataDto;
import java.util.Optional;

/**
 * Base cache repository for a specific {@link CacheDataDto} subtype and identifier type.
 *
 * @author godfrey.steven
 * @since 2026
 */
public abstract class CacheRepository<ID, T extends CacheDataDto> {

    protected abstract String getKey(ID id);

    public abstract T save(ID id, T value);

    public abstract Optional<T> findById(ID id);

    public abstract boolean deleteById(ID id);

    public abstract boolean existsById(ID id);
}