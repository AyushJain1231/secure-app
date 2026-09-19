package com.secureworld.secure.repository;

import com.secureworld.secure.Entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
}
