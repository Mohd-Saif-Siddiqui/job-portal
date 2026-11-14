package com.jobportal.seeker.entity;

import com.jobportal.auth.entity.User;
import com.jobportal.job.entity.Job;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "saved_jobs", uniqueConstraints = @UniqueConstraint(columnNames = {"seeker_id", "job_id"}))
@Getter @Setter @NoArgsConstructor
public class SavedJob {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seeker_id")
    private User seeker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(nullable = false)
    private Instant savedAt = Instant.now();
}
