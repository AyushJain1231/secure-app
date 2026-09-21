package com.secureworld.secure.cache;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

public record CacheEntryRequest(
        @NotNull(message = "Cache key is required") JsonNode key,
        @NotNull(message = "Cache value is required") JsonNode value) {
}
