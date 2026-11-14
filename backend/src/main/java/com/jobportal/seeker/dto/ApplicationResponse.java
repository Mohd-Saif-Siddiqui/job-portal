package com.jobportal.seeker.dto;

import com.jobportal.application.entity.Application;
import java.time.Instant;

public record ApplicationResponse(Long id, String status, String coverNote, Instant appliedAt, Instant updatedAt,
                                  Long jobId, String jobTitle, String companyName, String location,
                                  Long salaryMin, Long salaryMax) {
    public static ApplicationResponse from(Application a) {
        var j = a.getJob();
        return new ApplicationResponse(a.getId(), a.getStatus().name(), a.getCoverNote(), a.getAppliedAt(),
                a.getUpdatedAt(), j.getId(), j.getTitle(), j.getCompanyName(), j.getLocation(),
                j.getSalaryMin(), j.getSalaryMax());
    }
}
