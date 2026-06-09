package com.niraj.userservice.service;

import com.niraj.userservice.dto.LoginRequest;
import com.niraj.userservice.dto.LoginResponse;
import com.niraj.userservice.dto.RegisterRequest;
import com.niraj.userservice.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}