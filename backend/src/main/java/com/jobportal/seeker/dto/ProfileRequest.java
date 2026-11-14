package com.jobportal.seeker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record ProfileRequest(
        @NotBlank @Size(max = 100) String fullName,
        @Size(max = 150) String headline,
        @Size(max = 100) String location,
        @Size(max = 2000) String bio,
        @PositiveOrZero Long expectedSalary,
        @Valid List<SkillEntry> skills) {
}
