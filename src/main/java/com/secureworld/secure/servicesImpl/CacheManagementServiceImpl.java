package com.secureworld.secure.servicesImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.secureworld.secure.cache.CacheEntryRequest;
import com.secureworld.secure.cache.CacheRecordView;
import com.secureworld.secure.cache.CacheView;
import com.secureworld.secure.services.CacheManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class CacheManagementServiceImpl implements CacheManagementService {

    private static final Logger logger = LoggerFactory.getLogger(CacheManagementServiceImpl.class);
    private static final Pattern SENSITIVE_NAME =
            Pattern.compile(".*(password|passwd|token|secret|credential|authorization|jwt).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern JWT_VALUE =
            Pattern.compile("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    public CacheManagementServiceImpl(CacheManager cacheManager, ObjectMapper objectMapper) {
        this.cacheManager = cacheManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<CacheView> getCaches() {
        return cacheManager.getCacheNames().stream()
                .sorted()
                .map(this::toCacheView)
                .toList();
    }

    @Override
    public CacheRecordView put(String cacheName, CacheEntryRequest request, String actor) {
        CaffeineCache cache = getCache(cacheName);
        cache.put(request.key(), request.value());
        logger.info("Cache record updated: cacheName={}, actor={}", cacheName, actor);
        return toRecord(cacheName, request.key(), request.value());
    }

    @Override
    public void clear(String cacheName, String actor) {
        CaffeineCache cache = getCache(cacheName);
        cache.clear();
        logger.info("Cache cleared: cacheName={}, actor={}", cacheName, actor);
    }

    private CacheView toCacheView(String cacheName) {
        CaffeineCache cache = getCache(cacheName);
        Cache<Object, Object> nativeCache = cache.getNativeCache();
        List<CacheRecordView> records = nativeCache.asMap().entrySet().stream()
                .map(entry -> toRecord(cacheName, entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(record -> String.valueOf(record.key())))
                .toList();

        return new CacheView(
                cacheName,
                nativeCache.estimatedSize(),
                nativeCache.stats().hitCount(),
                nativeCache.stats().missCount(),
                nativeCache.stats().evictionCount(),
                records);
    }

    private CacheRecordView toRecord(String cacheName, Object key, Object value) {
        return new CacheRecordView(
                sanitize(cacheName, key),
                sanitize(cacheName, value),
                value == null ? "null" : value.getClass().getName());
    }

    private Object sanitize(String cacheName, Object value) {
        if (value == null) {
            return isSensitive(cacheName) ? "[REDACTED]" : null;
        }

        try {
            return isSensitive(cacheName)
                    ? "[REDACTED]"
                    : toJsonValue(sanitizeNode(objectMapper.valueToTree(value)));
        } catch (IllegalArgumentException exception) {
            return "[REDACTED_UNSERIALIZABLE_VALUE]";
        }
    }

    private JsonNode sanitizeNode(JsonNode node) {
        if (node.isTextual() && JWT_VALUE.matcher(node.textValue()).matches()) {
            return TextNode.valueOf("[REDACTED]");
        }
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            Map<String, JsonNode> sanitizedFields = new LinkedHashMap<>();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                sanitizedFields.put(field.getKey(), SENSITIVE_NAME.matcher(field.getKey()).matches()
                        ? TextNode.valueOf("[REDACTED]")
                        : sanitizeNode(field.getValue()));
            }
            return objectMapper.valueToTree(sanitizedFields);
        }
        if (node.isArray()) {
            List<JsonNode> sanitizedValues = new ArrayList<>();
            node.forEach(value -> sanitizedValues.add(sanitizeNode(value)));
            return objectMapper.valueToTree(sanitizedValues);
        }
        return node;
    }

    private Object toJsonValue(JsonNode node) {
        if (node.isNull()) {
            return null;
        }
        if (node.isObject()) {
            Map<String, Object> values = new LinkedHashMap<>();
            node.fields().forEachRemaining(field ->
                    values.put(field.getKey(), toJsonValue(field.getValue())));
            return values;
        }
        if (node.isArray()) {
            List<Object> values = new ArrayList<>();
            node.forEach(value -> values.add(toJsonValue(value)));
            return values;
        }
        if (node.isTextual()) {
            return node.textValue();
        }
        if (node.isBoolean()) {
            return node.booleanValue();
        }
        if (node.isNumber()) {
            return node.numberValue();
        }
        return node.toString();
    }

    private boolean isSensitive(String value) {
        return value != null && SENSITIVE_NAME.matcher(value).matches();
    }

    private CaffeineCache getCache(String cacheName) {
        if (cacheName == null || !cacheManager.getCacheNames().contains(cacheName)) {
            throw new CacheNotFoundException(cacheName);
        }
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (!(cache instanceof CaffeineCache caffeineCache)) {
            throw new IllegalStateException("Cache is not backed by Caffeine: " + cacheName);
        }
        return caffeineCache;
    }

    public static class CacheNotFoundException extends RuntimeException {
        public CacheNotFoundException(String cacheName) {
            super("Cache not found: " + cacheName);
        }
    }
}
