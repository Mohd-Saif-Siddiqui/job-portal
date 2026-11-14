package com.jobportal.auth.service;

import com.jobportal.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Tells Spring Security how to load a user (by email) when checking a login. */
@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public AppUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return users.findByEmail(email)
                .map(u -> new org.springframework.security.core.userdetails.User(
                        u.getEmail(), u.getPasswordHash(),
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()))))
                .orElseThrow(() -> new UsernameNotFoundException("No such user"));
    }
}
