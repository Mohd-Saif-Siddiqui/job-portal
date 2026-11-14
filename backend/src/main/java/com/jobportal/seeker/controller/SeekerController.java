package com.jobportal.seeker.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seeker")
public class SeekerController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("module", "seeker", "status", "ok");
    }
}
