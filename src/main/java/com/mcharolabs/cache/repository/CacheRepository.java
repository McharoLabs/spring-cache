package com.mcharolabs.cache.repository;

import com.mcharolabs.cache.dto.CacheDataDto;
import java.util.Optional;

/**
 * @author godfrey.steven
 * @since 2026
 */
public interface CacheRepository<ID, T extends CacheDataDto> {

    T save(ID id, T value);

    Optional<T> findById(ID id);

    boolean deleteById(ID id);

    boolean existsById(ID id);
}