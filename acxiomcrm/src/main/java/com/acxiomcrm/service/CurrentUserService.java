package com.acxiomcrm.service;

import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final AppUserRepository userRepository;

    public CurrentUserService(
            AppUserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public AppUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Current user not found"
                        )
                );
    }
}