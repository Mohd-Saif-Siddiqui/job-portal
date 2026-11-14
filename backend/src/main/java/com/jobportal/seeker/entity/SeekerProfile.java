package com.jobportal.seeker.entity;

import com.jobportal.auth.entity.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seeker_profiles")
@Getter @Setter @NoArgsConstructor
public class SeekerProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String headline;
    private String location;

    @Column(length = 2000)
    private String bio;

    /** Expected yearly salary in INR. */
    private Long expectedSalary;

    private String resumeStoredName;   // file name on disk
    private String resumeOriginalName; // name the user uploaded

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeekerSkill> skills = new ArrayList<>();
}
