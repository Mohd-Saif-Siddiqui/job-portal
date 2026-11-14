package com.jobportal.recruiter.service;

import com.jobportal.application.entity.Application;
import com.jobportal.application.entity.ApplicationStatus;
import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.User;
import com.jobportal.common.exception.ApiException;
import com.jobportal.common.security.CurrentUser;
import com.jobportal.common.storage.FileStorageService;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.service.SkillService;
import com.jobportal.recruiter.dto.ApplicantResponse;
import com.jobportal.recruiter.dto.JobRequest;
import com.jobportal.recruiter.dto.RecruiterJobResponse;
import com.jobportal.seeker.dto.SkillEntry;
import com.jobportal.seeker.entity.SeekerProfile;
import com.jobportal.seeker.repository.SeekerProfileRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecruiterJobService {
    private final CurrentUser currentUser;
    private final JobRepository jobs;
    private final ApplicationRepository applications;
    private final SeekerProfileRepository profiles;
    private final SkillService skillService;
    private final FileStorageService storage;

    public RecruiterJobService(CurrentUser currentUser, JobRepository jobs, ApplicationRepository applications,
                               SeekerProfileRepository profiles, SkillService skillService, FileStorageService storage) {
        this.currentUser = currentUser;
        this.jobs = jobs;
        this.applications = applications;
        this.profiles = profiles;
        this.skillService = skillService;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public List<RecruiterJobResponse> myJobs() {
        User u = currentUser.get();
        return jobs.findByPostedByIdOrderByPostedAtDesc(u.getId()).stream()
                .map(j -> RecruiterJobResponse.from(j, applications.countByJobId(j.getId()))).toList();
    }

    @Transactional(readOnly = true)
    public RecruiterJobResponse getJob(Long id) {
        Job j = ownedJob(id);
        return RecruiterJobResponse.from(j, applications.countByJobId(j.getId()));
    }

    @Transactional
    public RecruiterJobResponse create(JobRequest req) {
        Job j = new Job();
        j.setPostedBy(currentUser.get());
        j.setPostedAt(Instant.now());
        apply(j, req);
        jobs.save(j);
        return RecruiterJobResponse.from(j, 0);
    }

    @Transactional
    public RecruiterJobResponse update(Long id, JobRequest req) {
        Job j = ownedJob(id);
        apply(j, req);
        return RecruiterJobResponse.from(j, applications.countByJobId(j.getId()));
    }

    @Transactional
    public RecruiterJobResponse setStatus(Long id, JobStatus status) {
        Job j = ownedJob(id);
        j.setStatus(status);
        return RecruiterJobResponse.from(j, applications.countByJobId(j.getId()));
    }

    @Transactional(readOnly = true)
    public List<ApplicantResponse> applicants(Long jobId) {
        ownedJob(jobId);
        return applications.findByJobIdOrderByAppliedAtDesc(jobId).stream().map(this::toApplicant).toList();
    }

    @Transactional
    public ApplicantResponse setApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application a = ownedApplication(applicationId);
        a.setStatus(status);
        a.setUpdatedAt(Instant.now());
        return toApplicant(a);
    }

    @Transactional(readOnly = true)
    public String applicantResumeName(Long applicationId) {
        return resumeOf(ownedApplication(applicationId)).getResumeOriginalName();
    }

    @Transactional(readOnly = true)
    public Resource applicantResume(Long applicationId) {
        return storage.load(resumeOf(ownedApplication(applicationId)).getResumeStoredName());
    }

    // ---- helpers ----

    private SeekerProfile resumeOf(Application a) {
        SeekerProfile p = profiles.findByUserId(a.getSeeker().getId()).orElseThrow(() -> ApiException.notFound("No resume"));
        if (p.getResumeStoredName() == null) throw ApiException.notFound("This applicant has not uploaded a resume");
        return p;
    }

    /** A recruiter may only touch jobs (and their applications) that they posted themselves. */
    private Job ownedJob(Long id) {
        Job j = jobs.findById(id).orElseThrow(() -> ApiException.notFound("Job not found"));
        if (j.getPostedBy() == null || !j.getPostedBy().getId().equals(currentUser.get().getId())) {
            throw ApiException.notFound("Job not found");
        }
        return j;
    }

    private Application ownedApplication(Long id) {
        Application a = applications.findById(id).orElseThrow(() -> ApiException.notFound("Application not found"));
        ownedJob(a.getJob().getId());
        return a;
    }

    private void apply(Job j, JobRequest req) {
        if (req.salaryMin() > req.salaryMax()) throw ApiException.badRequest("Minimum salary cannot be more than maximum salary");
        j.setTitle(req.title().trim());
        j.setCompanyName(req.companyName().trim());
        j.setLocation(req.location().trim());
        j.setRemote(req.remote());
        j.setSalaryMin(req.salaryMin());
        j.setSalaryMax(req.salaryMax());
        j.setDescription(req.description().trim());
        j.getSkills().clear();
        j.getSkills().addAll(skillService.findOrCreateAll(req.skills()));
    }

    private ApplicantResponse toApplicant(Application a) {
        User s = a.getSeeker();
        SeekerProfile p = profiles.findByUserId(s.getId()).orElse(null);
        List<SkillEntry> skills = p == null ? List.of() : p.getSkills().stream()
                .map(x -> new SkillEntry(x.getSkill().getName(), x.getYearsExperience())).toList();
        return new ApplicantResponse(a.getId(), a.getStatus().name(), a.getCoverNote(), a.getAppliedAt(),
                s.getFullName(), s.getEmail(),
                p == null ? null : p.getHeadline(), p == null ? null : p.getLocation(),
                p == null ? null : p.getExpectedSalary(), p != null && p.getResumeStoredName() != null, skills);
    }
}
