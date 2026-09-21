package com.secureworld.secure.services;

import com.secureworld.secure.cache.CacheEntryRequest;
import com.secureworld.secure.cache.CacheRecordView;
import com.secureworld.secure.cache.CacheView;

import java.util.List;

public interface CacheManagementService {

    List<CacheView> getCaches();

    CacheRecordView put(String cacheName, CacheEntryRequest request, String actor);

    void clear(String cacheName, String actor);
}
