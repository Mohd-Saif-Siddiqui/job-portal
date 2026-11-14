package com.jobportal.seeker.service;

import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.User;
import com.jobportal.common.dto.PageResponse;
import com.jobportal.common.exception.ApiException;
import com.jobportal.common.security.CurrentUser;
import com.jobportal.job.dto.JobResponse;
import com.jobportal.job.dto.SearchCriteria;
import com.jobportal.job.entity.Job;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.service.JobSpecs;
import com.jobportal.seeker.entity.SavedJob;
import com.jobportal.seeker.repository.SavedJobRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Searching jobs, viewing one, and saving/unsaving, all from the logged-in seeker's point of view. */
@Service
public class SeekerJobService {
    private final CurrentUser currentUser;
    private final JobRepository jobs;
    private final SavedJobRepository savedJobs;
    private final ApplicationRepository applications;

    public SeekerJobService(CurrentUser currentUser, JobRepository jobs, SavedJobRepository savedJobs,
                            ApplicationRepository applications) {
        this.currentUser = currentUser;
        this.jobs = jobs;
        this.savedJobs = savedJobs;
        this.applications = applications;
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> search(SearchCriteria criteria, int page, int size) {
        User u = currentUser.get();
        Page<Job> result = jobs.findAll(JobSpecs.search(criteria, null),
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), Sort.by(Sort.Direction.DESC, "postedAt", "id")));
        return PageResponse.of(result.map(toResponse(u.getId())));
    }

    @Transactional(readOnly = true)
    public JobResponse get(Long id) {
        User u = currentUser.get();
        Job j = jobs.findById(id).orElseThrow(() -> ApiException.notFound("Job not found"));
        return toResponse(u.getId()).apply(j);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> listSaved() {
        User u = currentUser.get();
        var mapper = toResponse(u.getId());
        return savedJobs.findBySeekerIdOrderBySavedAtDesc(u.getId()).stream().map(s -> mapper.apply(s.getJob())).toList();
    }

    @Transactional
    public void save(Long jobId) {
        User u = currentUser.get();
        if (savedJobs.findBySeekerIdAndJobId(u.getId(), jobId).isPresent()) return; // already saved
        Job j = jobs.findById(jobId).orElseThrow(() -> ApiException.notFound("Job not found"));
        SavedJob s = new SavedJob();
        s.setSeeker(u);
        s.setJob(j);
        savedJobs.save(s);
    }

    @Transactional
    public void unsave(Long jobId) {
        User u = currentUser.get();
        savedJobs.findBySeekerIdAndJobId(u.getId(), jobId).ifPresent(savedJobs::delete);
    }

    /** Builds a function that turns a Job into a JobResponse with this seeker's saved/applied flags. */
    private java.util.function.Function<Job, JobResponse> toResponse(Long userId) {
        Set<Long> saved = savedJobs.findBySeekerIdOrderBySavedAtDesc(userId).stream()
                .map(s -> s.getJob().getId()).collect(Collectors.toCollection(HashSet::new));
        Map<Long, String> applied = applications.findBySeekerId(userId).stream()
                .collect(Collectors.toMap(a -> a.getJob().getId(), a -> a.getStatus().name()));
        return j -> JobResponse.from(j, saved.contains(j.getId()), applied.get(j.getId()));
    }
}
