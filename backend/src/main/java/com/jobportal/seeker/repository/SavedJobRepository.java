package com.jobportal.seeker.repository;

import com.jobportal.seeker.entity.SavedJob;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findBySeekerIdOrderBySavedAtDesc(Long seekerId);
    Optional<SavedJob> findBySeekerIdAndJobId(Long seekerId, Long jobId);
}
