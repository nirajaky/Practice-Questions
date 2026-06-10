package com.niraj.userservice.dto;

import com.niraj.userservice.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private Role role;
}