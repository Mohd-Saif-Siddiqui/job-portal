package com.jobportal.common.security;

import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Gives services the logged-in user (the session stores the email as the username). */
@Component
public class CurrentUser {
    private final UserRepository users;

    public CurrentUser(UserRepository users) {
        this.users = users;
    }

    public User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please log in");
        }
        return users.findByEmail(auth.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Please log in"));
    }
}
