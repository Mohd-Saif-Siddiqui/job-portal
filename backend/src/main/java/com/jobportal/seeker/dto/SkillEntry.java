package com.jobportal.seeker.dto;

import jakarta.validation.constraints.*;

public record SkillEntry(@NotBlank @Size(max = 50) String name, @Min(0) @Max(50) int yearsExperience) {
}
