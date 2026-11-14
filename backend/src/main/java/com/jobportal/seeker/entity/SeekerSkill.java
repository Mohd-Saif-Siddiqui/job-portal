package com.jobportal.seeker.entity;

import com.jobportal.job.entity.Skill;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seeker_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "skill_id"}))
@Getter @Setter @NoArgsConstructor
public class SeekerSkill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private SeekerProfile profile;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private int yearsExperience;
}
