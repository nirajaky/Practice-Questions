package com.niraj.userservice.service.impl;

import com.niraj.userservice.dto.UserResponse;
import com.niraj.userservice.entity.Role;
import com.niraj.userservice.entity.User;
import com.niraj.userservice.repository.UserRepository;
import com.niraj.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public void promoteToAdmin(Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow();

        user.setRole(Role.ADMIN);

        userRepository.save(user);
    }
}