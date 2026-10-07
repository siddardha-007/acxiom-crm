package com.acxiomcrm.controller;

import com.acxiomcrm.dto.CreateUserRequest;
import com.acxiomcrm.dto.ResetPasswordRequest;
import com.acxiomcrm.dto.UpdateUserRequest;
import com.acxiomcrm.dto.UserResponse;
import com.acxiomcrm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>>
    getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse>
    getUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUser(id)
        );
    }

    @PostMapping
    public ResponseEntity<UserResponse>
    createUser(
            @Valid @RequestBody
            CreateUserRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userService.createUser(
                                request
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse>
    updateUser(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateUserRequest request
    ) {

        return ResponseEntity.ok(
                userService.updateUser(
                        id,
                        request
                )
        );
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void>
    activateUser(
            @PathVariable Long id
    ) {

        userService.activateUser(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void>
    deactivateUser(
            @PathVariable Long id
    ) {

        userService.deactivateUser(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Void>
    resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody
            ResetPasswordRequest request
    ) {

        userService.resetPassword(
                id,
                request
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<Void>
    unlockUser(
            @PathVariable Long id
    ) {

        userService.unlockUser(id);

        return ResponseEntity.noContent().build();
    }
}