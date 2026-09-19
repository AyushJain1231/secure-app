package com.secureworld.secure.controller;

import com.secureworld.secure.auth.AuthService;
import com.secureworld.secure.auth.JwtService;
import com.secureworld.secure.auth.dto.AuthResponse;
import com.secureworld.secure.auth.dto.LoginRequest;
import com.secureworld.secure.auth.dto.RegisterRequest;
import com.secureworld.secure.auth.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(authService.register(request)));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userId(), request.password()));
        String token = jwtService.generateToken((org.springframework.security.core.userdetails.UserDetails)
                authentication.getPrincipal());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }
}
