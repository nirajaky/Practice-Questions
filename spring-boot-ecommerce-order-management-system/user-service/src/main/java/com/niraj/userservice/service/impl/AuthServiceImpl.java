package com.niraj.userservice.service.impl;

import com.niraj.userservice.dto.LoginRequest;
import com.niraj.userservice.dto.LoginResponse;
import com.niraj.userservice.dto.RegisterRequest;
import com.niraj.userservice.dto.RegisterResponse;
import com.niraj.userservice.entity.Role;
import com.niraj.userservice.entity.User;
import com.niraj.userservice.exception.UserAlreadyExistsException;
import com.niraj.userservice.repository.UserRepository;
import com.niraj.userservice.service.AuthService;
import com.niraj.userservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + request.getEmail()
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new RegisterResponse("User registered successfully");
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token);
    }
}