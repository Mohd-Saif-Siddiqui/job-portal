package com.jobportal.job.repository;

import com.jobportal.job.entity.Job;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    List<Job> findByPostedByIdOrderByPostedAtDesc(Long userId);
}
