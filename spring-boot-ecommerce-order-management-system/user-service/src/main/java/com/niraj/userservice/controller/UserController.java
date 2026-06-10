package com.niraj.userservice.controller;

import com.niraj.userservice.dto.UserResponse;
import com.niraj.userservice.entity.User;
import com.niraj.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.
        AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getProfile(
            @AuthenticationPrincipal User user) {

        return userService.getCurrentUser(
                user.getEmail());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {

        return "Welcome Admin";
    }

    @PutMapping("/{id}/promote")
    public String promote(
            @PathVariable Long id) {

        userService.promoteToAdmin(id);

        return "User promoted";
    }
}