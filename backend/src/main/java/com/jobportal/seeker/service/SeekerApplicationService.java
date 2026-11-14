package com.jobportal.seeker.service;

import com.jobportal.application.entity.Application;
import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.User;
import com.jobportal.common.exception.ApiException;
import com.jobportal.common.security.CurrentUser;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.seeker.dto.ApplicationResponse;
import com.jobportal.seeker.dto.ApplyRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeekerApplicationService {
    private final CurrentUser currentUser;
    private final JobRepository jobs;
    private final ApplicationRepository applications;

    public SeekerApplicationService(CurrentUser currentUser, JobRepository jobs, ApplicationRepository applications) {
        this.currentUser = currentUser;
        this.jobs = jobs;
        this.applications = applications;
    }

    @Transactional
    public ApplicationResponse apply(ApplyRequest req) {
        User u = currentUser.get();
        Job job = jobs.findById(req.jobId()).orElseThrow(() -> ApiException.notFound("Job not found"));
        if (job.getStatus() != JobStatus.OPEN) throw ApiException.badRequest("This job is no longer accepting applications");
        if (applications.existsBySeekerIdAndJobId(u.getId(), job.getId())) {
            throw ApiException.conflict("You have already applied to this job");
        }
        Application a = new Application();
        a.setSeeker(u);
        a.setJob(job);
        a.setCoverNote(req.coverNote() == null || req.coverNote().isBlank() ? null : req.coverNote().trim());
        a.setAppliedAt(Instant.now());
        a.setUpdatedAt(a.getAppliedAt());
        applications.save(a);
        return ApplicationResponse.from(a);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> mine() {
        User u = currentUser.get();
        return applications.findBySeekerIdOrderByAppliedAtDesc(u.getId()).stream().map(ApplicationResponse::from).toList();
    }
}
