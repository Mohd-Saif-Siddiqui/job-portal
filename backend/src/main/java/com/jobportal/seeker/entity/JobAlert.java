package com.jobportal.seeker.entity;

import com.jobportal.auth.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A saved search. "New matches" = open jobs matching it that were posted after lastSeenAt. */
@Entity
@Table(name = "job_alerts")
@Getter @Setter @NoArgsConstructor
public class JobAlert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seeker_id")
    private User seeker;

    @Column(nullable = false)
    private String name;

    private String q;
    private String skills; // comma-separated skill names
    private String location;
    private boolean remote;
    private Long minSalary;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant lastSeenAt = Instant.now();
}
