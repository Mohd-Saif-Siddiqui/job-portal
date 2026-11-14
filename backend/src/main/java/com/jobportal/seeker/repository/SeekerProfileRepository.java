package com.jobportal.seeker.repository;

import com.jobportal.seeker.entity.SeekerProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeekerProfileRepository extends JpaRepository<SeekerProfile, Long> {
    Optional<SeekerProfile> findByUserId(Long userId);
}
