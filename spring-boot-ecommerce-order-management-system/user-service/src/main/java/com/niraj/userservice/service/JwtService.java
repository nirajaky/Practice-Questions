package com.niraj.userservice.service;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateToken(String username);

    String extractUsername(String token);

    boolean isTokenValid(String token);
}