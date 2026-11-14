package com.jobportal.auth.service;

import com.jobportal.auth.dto.RegisterRequest;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.common.exception.ApiException;
import com.jobportal.seeker.entity.SeekerProfile;
import com.jobportal.seeker.repository.SeekerProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final SeekerProfileRepository profiles;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, SeekerProfileRepository profiles, PasswordEncoder encoder) {
        this.users = users;
        this.profiles = profiles;
        this.encoder = encoder;
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    @Transactional
    public User register(RegisterRequest req) {
        String email = normalizeEmail(req.email());
        if (users.existsByEmail(email)) {
            throw ApiException.conflict("An account with this email already exists");
        }
        User u = new User();
        u.setEmail(email);
        u.setFullName(req.fullName().trim());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(req.role());
        users.save(u);
        if (u.getRole() == Role.SEEKER) {
            SeekerProfile p = new SeekerProfile();
            p.setUser(u);
            profiles.save(p);
        }
        return u;
    }
}
