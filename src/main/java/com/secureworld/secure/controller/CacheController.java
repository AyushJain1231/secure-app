package com.secureworld.secure.controller;

import com.secureworld.secure.cache.CacheEntryRequest;
import com.secureworld.secure.cache.CacheRecordView;
import com.secureworld.secure.cache.CacheView;
import com.secureworld.secure.services.CacheManagementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final CacheManagementService cacheManagementService;

    public CacheController(CacheManagementService cacheManagementService) {
        this.cacheManagementService = cacheManagementService;
    }

    @GetMapping
    public ResponseEntity<List<CacheView>> getCaches() {
        return ResponseEntity.ok(cacheManagementService.getCaches());
    }

    @PutMapping("/{cacheName}")
    public ResponseEntity<CacheRecordView> put(
            @PathVariable String cacheName,
            @Valid @RequestBody CacheEntryRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(cacheManagementService.put(cacheName, request, authentication.getName()));
    }

    @DeleteMapping("/{cacheName}")
    public ResponseEntity<Void> clear(
            @PathVariable String cacheName,
            Authentication authentication) {
        cacheManagementService.clear(cacheName, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
