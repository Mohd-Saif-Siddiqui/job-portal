package com.jobportal.recruiter.dto;

import com.jobportal.application.entity.ApplicationStatus;
import com.jobportal.job.entity.JobStatus;
import jakarta.validation.constraints.NotNull;

public final class StatusRequests {
    private StatusRequests() {}

    public record JobStatusRequest(@NotNull JobStatus status) {}

    public record ApplicationStatusRequest(@NotNull ApplicationStatus status) {}
}
