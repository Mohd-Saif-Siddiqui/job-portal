package com.jobportal.recruiter.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record JobRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 150) String companyName,
        @NotBlank @Size(max = 100) String location,
        boolean remote,
        @NotNull @Positive Long salaryMin,
        @NotNull @Positive Long salaryMax,
        @NotBlank @Size(max = 5000) String description,
        List<String> skills) {
}
