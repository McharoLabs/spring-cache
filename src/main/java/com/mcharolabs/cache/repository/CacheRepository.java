package com.mcharolabs.cache.repository;

import com.mcharolabs.cache.dto.CacheDataDto;
import java.util.Optional;

/**
 * Base cache repository interface for a {@link CacheDataDto} subtype.
 *
 * @author godfrey.steven
 * @since 2026
 */
public interface CacheRepository<ID, T extends CacheDataDto> {

    String getKey(ID id);

    T save(ID id, T value);

    Optional<T> findById(ID id);

    boolean deleteById(ID id);

    boolean existsById(ID id);
}