package com.acxiomcrm.service;

import com.acxiomcrm.dto.AuthResponse;
import com.acxiomcrm.dto.LoginRequest;
import com.acxiomcrm.dto.RegisterRequest;
import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.enums.Role;
import com.acxiomcrm.repository.AppUserRepository;
import com.acxiomcrm.security.CustomUserDetails;
import com.acxiomcrm.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        AppUser user = new AppUser();

        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        // Public registration creates Sales Executive.
        user.setRole(Role.SALES_EXECUTIVE);
        user.setActive(true);

        userRepository.save(user);

        auditService.log(
                user,
                "REGISTER",
                "USER",
                user.getId(),
                null,
                "User registered",
                null
        );

        String token = jwtService.generateToken(
                new CustomUserDetails(user)
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {

        AppUser user = userRepository
                .findByEmail(request.email().toLowerCase())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "Account is inactive"
            );
        }

        if (user.getLockoutEnd() != null &&
                user.getLockoutEnd()
                        .isAfter(LocalDateTime.now())) {

            throw new IllegalStateException(
                    "Account is temporarily locked"
            );
        }

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email().toLowerCase(),
                            request.password()
                    )
            );

            user.setFailedLoginAttempts(0);
            user.setLockoutEnd(null);

            userRepository.save(user);

            auditService.log(
                    user,
                    "LOGIN_SUCCESS",
                    "AUTHENTICATION",
                    user.getId(),
                    null,
                    "Successful login",
                    null
            );

            String token =
                    jwtService.generateToken(
                            new CustomUserDetails(user)
                    );

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
            );

        } catch (BadCredentialsException ex) {

            handleFailedLogin(user);

            auditService.log(
                    user,
                    "LOGIN_FAILED",
                    "AUTHENTICATION",
                    user.getId(),
                    null,
                    "Invalid credentials",
                    null
            );

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }
    }

    private void handleFailedLogin(AppUser user) {

        int attempts =
                user.getFailedLoginAttempts() + 1;

        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {

            user.setLockoutEnd(
                    LocalDateTime.now()
                            .plusMinutes(LOCKOUT_MINUTES)
            );

            user.setFailedLoginAttempts(0);
        }

        userRepository.save(user);
    }
}