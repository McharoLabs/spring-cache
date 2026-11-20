package com.mcharolabs.cache.service;

import com.mcharolabs.cache.dto.CacheDataDto;
import java.util.Optional;

/**
 * @author godfrey.steven
 * @since 2026
 */
public interface CacheService {

    <T extends CacheDataDto> Optional<T> get(String key, Class<T> type);

    <T extends CacheDataDto> T set(String key, T value);

    boolean delete(String key);

    boolean clear();
}