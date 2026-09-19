package com.secureworld.secure.auth;

import com.secureworld.secure.Entity.AppUser;
import com.secureworld.secure.Entity.Role;
import com.secureworld.secure.auth.dto.RegisterRequest;
import com.secureworld.secure.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(RegisterRequest request) {
        String userId = request.userId().trim();
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByUserId(userId)) {
            throw new DuplicateUserException("User ID is already registered");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("Email is already registered");
        }
        return userRepository.save(AppUser.builder()
                .userId(userId)
                .email(email)
                .name(request.name().trim())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build());
    }
}
