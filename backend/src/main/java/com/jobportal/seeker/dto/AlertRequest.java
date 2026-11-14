package com.jobportal.seeker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AlertRequest(@NotBlank @Size(max = 100) String name, String q, List<String> skills,
                           String location, Boolean remote, Long minSalary) {
}
