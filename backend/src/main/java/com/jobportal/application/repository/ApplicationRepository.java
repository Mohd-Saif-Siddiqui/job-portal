package com.jobportal.application.repository;

import com.jobportal.application.entity.Application;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findBySeekerIdOrderByAppliedAtDesc(Long seekerId);
    List<Application> findBySeekerId(Long seekerId);
    List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);
    boolean existsBySeekerIdAndJobId(Long seekerId, Long jobId);
    long countByJobId(Long jobId);
}
