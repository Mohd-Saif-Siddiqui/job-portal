package com.jobportal.recruiter.dto;

import com.jobportal.job.entity.Job;
import java.time.Instant;
import java.util.List;

public record RecruiterJobResponse(Long id, String title, String companyName, String location, boolean remote,
                                   Long salaryMin, Long salaryMax, String description, String status,
                                   Instant postedAt, List<String> skills, long applicantCount) {
    public static RecruiterJobResponse from(Job j, long applicantCount) {
        return new RecruiterJobResponse(j.getId(), j.getTitle(), j.getCompanyName(), j.getLocation(), j.isRemote(),
                j.getSalaryMin(), j.getSalaryMax(), j.getDescription(), j.getStatus().name(), j.getPostedAt(),
                j.getSkills().stream().map(s -> s.getName()).sorted().toList(), applicantCount);
    }
}
