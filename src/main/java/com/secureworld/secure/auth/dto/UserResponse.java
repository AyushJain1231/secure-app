package com.secureworld.secure.auth.dto;

import com.secureworld.secure.Entity.AppUser;

import java.time.Instant;

public record UserResponse(Long id, String userId, String email, String name, String role, Instant createdDate) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getUserId(), user.getEmail(), user.getName(),
                user.getRole().name(), user.getCreatedDate());
    }
}
