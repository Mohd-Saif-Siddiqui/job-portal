package com.jobportal.seeker.service;

import com.jobportal.auth.entity.User;
import com.jobportal.common.exception.ApiException;
import com.jobportal.common.security.CurrentUser;
import com.jobportal.job.dto.SearchCriteria;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.service.JobSpecs;
import com.jobportal.seeker.dto.AlertRequest;
import com.jobportal.seeker.dto.AlertResponse;
import com.jobportal.seeker.entity.JobAlert;
import com.jobportal.seeker.repository.JobAlertRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Job alerts = saved searches that tell the seeker how many new matching jobs appeared. */
@Service
public class AlertService {
    private final CurrentUser currentUser;
    private final JobAlertRepository alerts;
    private final JobRepository jobs;

    public AlertService(CurrentUser currentUser, JobAlertRepository alerts, JobRepository jobs) {
        this.currentUser = currentUser;
        this.alerts = alerts;
        this.jobs = jobs;
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> list() {
        User u = currentUser.get();
        return alerts.findBySeekerIdOrderByCreatedAtDesc(u.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public AlertResponse create(AlertRequest req) {
        User u = currentUser.get();
        JobAlert a = new JobAlert();
        a.setSeeker(u);
        a.setName(req.name().trim());
        a.setQ(blankToNull(req.q()));
        a.setSkills(req.skills() == null || req.skills().isEmpty() ? null : String.join(",", req.skills()));
        a.setLocation(blankToNull(req.location()));
        a.setRemote(Boolean.TRUE.equals(req.remote()));
        a.setMinSalary(req.minSalary());
        alerts.save(a);
        return toResponse(a);
    }

    @Transactional
    public void delete(Long id) {
        alerts.delete(owned(id));
    }

    /** Called when the seeker opens the alert's results: resets the "new" counter. */
    @Transactional
    public void markSeen(Long id) {
        JobAlert a = owned(id);
        a.setLastSeenAt(Instant.now());
        alerts.save(a);
    }

    private JobAlert owned(Long id) {
        User u = currentUser.get();
        JobAlert a = alerts.findById(id).orElseThrow(() -> ApiException.notFound("Alert not found"));
        if (!a.getSeeker().getId().equals(u.getId())) throw ApiException.notFound("Alert not found");
        return a;
    }

    private AlertResponse toResponse(JobAlert a) {
        List<String> skills = a.getSkills() == null ? List.of() : Arrays.asList(a.getSkills().split(","));
        SearchCriteria c = new SearchCriteria(a.getQ(), skills, a.getLocation(), a.isRemote(), a.getMinSalary());
        long newCount = jobs.count(JobSpecs.search(c, a.getLastSeenAt()));
        return new AlertResponse(a.getId(), a.getName(), a.getQ(), skills, a.getLocation(), a.isRemote(),
                a.getMinSalary(), newCount, a.getCreatedAt());
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
