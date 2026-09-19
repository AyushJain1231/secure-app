package com.secureworld.secure.auth;

import com.secureworld.secure.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository userRepository;

    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUserId(username)
                .map(user -> User.withUsername(user.getUserId())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid user ID or password"));
    }
}
