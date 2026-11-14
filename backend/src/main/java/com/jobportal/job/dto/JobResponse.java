package com.jobportal.job.dto;

import com.jobportal.job.entity.Job;
import java.time.Instant;
import java.util.List;

public record JobResponse(
        Long id, String title, String companyName, String location, boolean remote,
        Long salaryMin, Long salaryMax, String description, String status, Instant postedAt,
        List<String> skills,
        boolean saved,            // has the logged-in seeker saved it?
        String applicationStatus  // null if the seeker hasn't applied
) {
    public static JobResponse from(Job j, boolean saved, String applicationStatus) {
        List<String> skills = j.getSkills().stream().map(s -> s.getName()).sorted().toList();
        return new JobResponse(j.getId(), j.getTitle(), j.getCompanyName(), j.getLocation(), j.isRemote(),
                j.getSalaryMin(), j.getSalaryMax(), j.getDescription(), j.getStatus().name(), j.getPostedAt(),
                skills, saved, applicationStatus);
    }
}
