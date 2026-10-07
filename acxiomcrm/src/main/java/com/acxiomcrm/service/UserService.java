package com.acxiomcrm.service;

import com.acxiomcrm.dto.CreateUserRequest;
import com.acxiomcrm.dto.ResetPasswordRequest;
import com.acxiomcrm.dto.UpdateUserRequest;
import com.acxiomcrm.dto.UserResponse;
import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.enums.Role;
import com.acxiomcrm.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public UserService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CurrentUserService currentUserService,
            AuditService auditService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUser(Long id) {

        AppUser user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        return toResponse(user);
    }

    @Transactional
    public UserResponse createUser(
            CreateUserRequest request
    ) {

        if (userRepository.existsByEmail(
                request.email().toLowerCase()
        )) {

            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        AppUser user = new AppUser();

        user.setName(request.name());

        user.setEmail(
                request.email().toLowerCase()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setRole(request.role());

        user.setActive(true);

        userRepository.save(user);

        AppUser admin =
                currentUserService.getCurrentUser();

        auditService.log(
                admin,
                "CREATE",
                "USER",
                user.getId(),
                null,
                "Created user: " + user.getEmail(),
                null
        );

        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        AppUser user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getId().equals(id)
                && !request.active()) {

            throw new IllegalArgumentException(
                    "You cannot deactivate your own account"
            );
        }

        if (!user.getEmail()
                .equalsIgnoreCase(request.email())
                && userRepository.existsByEmail(
                request.email().toLowerCase()
        )) {

            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        Role oldRole = user.getRole();

        user.setName(request.name());

        user.setEmail(
                request.email().toLowerCase()
        );

        user.setRole(request.role());

        user.setActive(request.active());

        AppUser saved =
                userRepository.save(user);

        if (oldRole != saved.getRole()) {

            auditService.log(
                    current,
                    "ROLE_CHANGE",
                    "USER",
                    id,
                    oldRole.name(),
                    saved.getRole().name(),
                    null
            );
        }

        auditService.log(
                current,
                "UPDATE",
                "USER",
                id,
                null,
                saved.getEmail(),
                null
        );

        return toResponse(saved);
    }

    @Transactional
    public void deactivateUser(Long id) {

        AppUser current =
                currentUserService.getCurrentUser();

        if (current.getId().equals(id)) {

            throw new IllegalArgumentException(
                    "You cannot deactivate your own account"
            );
        }

        AppUser user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        user.setActive(false);

        userRepository.save(user);

        auditService.log(
                current,
                "DEACTIVATE",
                "USER",
                id,
                "ACTIVE",
                "INACTIVE",
                null
        );
    }

    @Transactional
    public void activateUser(Long id) {

        AppUser user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        user.setActive(true);
        user.setFailedLoginAttempts(0);
        user.setLockoutEnd(null);

        userRepository.save(user);

        auditService.log(
                currentUserService.getCurrentUser(),
                "ACTIVATE",
                "USER",
                id,
                "INACTIVE",
                "ACTIVE",
                null
        );
    }

    @Transactional
    public void resetPassword(
            Long id,
            ResetPasswordRequest request
    ) {

        AppUser user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setFailedLoginAttempts(0);
        user.setLockoutEnd(null);

        userRepository.save(user);

        auditService.log(
                currentUserService.getCurrentUser(),
                "PASSWORD_RESET",
                "USER",
                id,
                null,
                "Password reset",
                null
        );
    }

    @Transactional
    public void unlockUser(Long id) {

        AppUser user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        user.setFailedLoginAttempts(0);
        user.setLockoutEnd(null);

        userRepository.save(user);

        auditService.log(
                currentUserService.getCurrentUser(),
                "UNLOCK",
                "USER",
                id,
                null,
                "Account unlocked",
                null
        );
    }

    private UserResponse toResponse(
            AppUser user
    ) {

        boolean locked =
                user.getLockoutEnd() != null
                        && user.getLockoutEnd()
                        .isAfter(LocalDateTime.now());

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getFailedLoginAttempts(),
                locked,
                user.getLockoutEnd(),
                user.getCreatedDate()
        );
    }
}