package com.jobportal.seeker.controller;

import com.jobportal.seeker.dto.ApplicationResponse;
import com.jobportal.seeker.dto.ApplyRequest;
import com.jobportal.seeker.service.SeekerApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seeker/applications")
public class SeekerApplicationController {
    private final SeekerApplicationService service;

    public SeekerApplicationController(SeekerApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(@Valid @RequestBody ApplyRequest body) {
        return service.apply(body);
    }

    @GetMapping
    public List<ApplicationResponse> mine() {
        return service.mine();
    }
}
