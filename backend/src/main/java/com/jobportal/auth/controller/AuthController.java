package com.jobportal.auth.controller;

import com.jobportal.auth.dto.LoginRequest;
import com.jobportal.auth.dto.RegisterRequest;
import com.jobportal.auth.dto.UserResponse;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.service.AuthService;
import com.jobportal.common.security.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authManager;
    private final SecurityContextRepository contextRepo;
    private final CurrentUser currentUser;

    public AuthController(AuthService authService, AuthenticationManager authManager,
                          SecurityContextRepository contextRepo, CurrentUser currentUser) {
        this.authService = authService;
        this.authManager = authManager;
        this.contextRepo = contextRepo;
        this.currentUser = currentUser;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest body,
                                 HttpServletRequest req, HttpServletResponse res) {
        User u = authService.register(body);
        startSession(u.getEmail(), body.password(), req, res); // log the new user in straight away
        return UserResponse.from(u);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest body,
                              HttpServletRequest req, HttpServletResponse res) {
        startSession(AuthService.normalizeEmail(body.email()), body.password(), req, res);
        return UserResponse.from(currentUser.get());
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping("/me")
    public UserResponse me() {
        return UserResponse.from(currentUser.get());
    }

    private Authentication startSession(String email, String password,
                                        HttpServletRequest req, HttpServletResponse res) {
        Authentication auth = authManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(email, password));
        if (req.getSession(false) != null) req.changeSessionId(); // protects against session fixation
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
        contextRepo.saveContext(ctx, req, res);
        return auth;
    }
}
