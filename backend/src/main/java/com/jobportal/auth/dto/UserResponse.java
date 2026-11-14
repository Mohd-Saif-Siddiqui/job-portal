package com.jobportal.auth.dto;

import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;

public record UserResponse(Long id, String email, String fullName, Role role) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getRole());
    }
}
