package com.jobportal.seeker.dto;

import java.util.List;

public record ProfileResponse(String fullName, String email, String headline, String location, String bio,
                              Long expectedSalary, String resumeName, List<SkillEntry> skills) {
}
