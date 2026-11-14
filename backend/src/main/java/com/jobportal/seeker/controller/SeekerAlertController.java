package com.jobportal.seeker.controller;

import com.jobportal.seeker.dto.AlertRequest;
import com.jobportal.seeker.dto.AlertResponse;
import com.jobportal.seeker.service.AlertService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seeker/alerts")
public class SeekerAlertController {
    private final AlertService service;

    public SeekerAlertController(AlertService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlertResponse> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlertResponse create(@Valid @RequestBody AlertRequest body) {
        return service.create(body);
    }

    @PostMapping("/{id}/seen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void seen(@PathVariable Long id) {
        service.markSeen(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
