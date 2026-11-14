package com.jobportal.seeker.repository;

import com.jobportal.seeker.entity.JobAlert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobAlertRepository extends JpaRepository<JobAlert, Long> {
    List<JobAlert> findBySeekerIdOrderByCreatedAtDesc(Long seekerId);
}
