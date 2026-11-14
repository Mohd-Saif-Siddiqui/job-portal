package com.jobportal.seeker.controller;

import com.jobportal.seeker.dto.ProfileRequest;
import com.jobportal.seeker.dto.ProfileResponse;
import com.jobportal.seeker.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/seeker/profile")
public class SeekerProfileController {
    private final ProfileService service;

    public SeekerProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public ProfileResponse get() {
        return service.get();
    }

    @PutMapping
    public ProfileResponse update(@Valid @RequestBody ProfileRequest body) {
        return service.update(body);
    }

    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileResponse uploadResume(@RequestParam("file") MultipartFile file) {
        return service.uploadResume(file);
    }

    @DeleteMapping("/resume")
    public ProfileResponse deleteResume() {
        return service.deleteResume();
    }

    @GetMapping("/resume")
    public ResponseEntity<Resource> downloadResume() {
        String name = service.resumeOriginalName();
        Resource file = service.resumeFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(name).build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}
