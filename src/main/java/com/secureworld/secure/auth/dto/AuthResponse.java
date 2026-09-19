package com.secureworld.secure.auth.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
