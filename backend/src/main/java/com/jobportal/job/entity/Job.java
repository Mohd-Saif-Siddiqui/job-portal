package com.jobportal.job.entity;

import com.jobportal.auth.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor
public class Job {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String companyName; // plain text for now; a Company table can come with the recruiter module

    @Column(nullable = false)
    private String location;

    private boolean remote;

    /** Yearly salary range in Indian rupees (INR). */
    @Column(nullable = false)
    private Long salaryMin;

    @Column(nullable = false)
    private Long salaryMax;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.OPEN;

    @Column(nullable = false)
    private Instant postedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by")
    private User postedBy;

    @ManyToMany
    @JoinTable(name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @BatchSize(size = 50)
    private Set<Skill> skills = new HashSet<>();
}
