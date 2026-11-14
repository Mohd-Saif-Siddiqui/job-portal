package com.jobportal.recruiter.controller;

import com.jobportal.recruiter.dto.ApplicantResponse;
import com.jobportal.recruiter.dto.JobRequest;
import com.jobportal.recruiter.dto.RecruiterJobResponse;
import com.jobportal.recruiter.dto.StatusRequests.ApplicationStatusRequest;
import com.jobportal.recruiter.dto.StatusRequests.JobStatusRequest;
import com.jobportal.recruiter.service.RecruiterJobService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterJobController {
    private final RecruiterJobService service;

    public RecruiterJobController(RecruiterJobService service) {
        this.service = service;
    }

    @GetMapping("/jobs")
    public List<RecruiterJobResponse> myJobs() {
        return service.myJobs();
    }

    @PostMapping("/jobs")
    @ResponseStatus(HttpStatus.CREATED)
    public RecruiterJobResponse create(@Valid @RequestBody JobRequest body) {
        return service.create(body);
    }

    @GetMapping("/jobs/{id}")
    public RecruiterJobResponse get(@PathVariable Long id) {
        return service.getJob(id);
    }

    @PutMapping("/jobs/{id}")
    public RecruiterJobResponse update(@PathVariable Long id, @Valid @RequestBody JobRequest body) {
        return service.update(id, body);
    }

    @PatchMapping("/jobs/{id}/status")
    public RecruiterJobResponse setStatus(@PathVariable Long id, @Valid @RequestBody JobStatusRequest body) {
        return service.setStatus(id, body.status());
    }

    @GetMapping("/jobs/{id}/applicants")
    public List<ApplicantResponse> applicants(@PathVariable Long id) {
        return service.applicants(id);
    }

    @PatchMapping("/applications/{id}/status")
    public ApplicantResponse setApplicationStatus(@PathVariable Long id, @Valid @RequestBody ApplicationStatusRequest body) {
        return service.setApplicationStatus(id, body.status());
    }

    @GetMapping("/applications/{id}/resume")
    public ResponseEntity<Resource> resume(@PathVariable Long id) {
        String name = service.applicantResumeName(id);
        Resource file = service.applicantResume(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(name).build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}
