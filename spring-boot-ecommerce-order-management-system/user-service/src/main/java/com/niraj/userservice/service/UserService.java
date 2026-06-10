package com.niraj.userservice.service;

import com.niraj.userservice.dto.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(String email);

    void promoteToAdmin(Long userId);
}