package com.jobportal.recruiter.dto;

import com.jobportal.seeker.dto.SkillEntry;
import java.time.Instant;
import java.util.List;

public record ApplicantResponse(Long applicationId, String status, String coverNote, Instant appliedAt,
                                String fullName, String email, String headline, String location,
                                Long expectedSalary, boolean hasResume, List<SkillEntry> skills) {
}
