package com.jobportal.application.entity;

import com.jobportal.auth.entity.User;
import com.jobportal.job.entity.Job;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = {"seeker_id", "job_id"}))
@Getter @Setter @NoArgsConstructor
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seeker_id")
    private User seeker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(length = 2000)
    private String coverNote;

    @Column(nullable = false)
    private Instant appliedAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();
}
