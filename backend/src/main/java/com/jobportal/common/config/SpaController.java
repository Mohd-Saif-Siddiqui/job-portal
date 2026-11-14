package com.jobportal.common.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * In production the built React app is served by Spring Boot itself (from /static).
 * React Router handles URLs like /seeker/jobs in the browser, so when someone refreshes the page
 * or opens such a link directly, the server must answer with index.html instead of a 404.
 * (During local development Vite serves the frontend, so this is not used.)
 */
@Controller
public class SpaController {

    @GetMapping({"/", "/login", "/register", "/seeker", "/seeker/**", "/recruiter", "/recruiter/**"})
    public String index() {
        return "forward:/index.html";
    }
}
