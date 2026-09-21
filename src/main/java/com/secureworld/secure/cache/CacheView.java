package com.secureworld.secure.cache;

import java.util.List;

public record CacheView(
        String name,
        long size,
        long hitCount,
        long missCount,
        long evictionCount,
        List<CacheRecordView> records) {
}
