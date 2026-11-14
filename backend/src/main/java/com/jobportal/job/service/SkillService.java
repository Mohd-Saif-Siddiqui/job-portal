package com.jobportal.job.service;

import com.jobportal.job.entity.Skill;
import com.jobportal.job.repository.SkillRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class SkillService {
    private final SkillRepository skills;

    public SkillService(SkillRepository skills) {
        this.skills = skills;
    }

    /** Finds a skill by name (case-insensitive) or creates it. */
    public Skill findOrCreate(String name) {
        String clean = name.trim();
        return skills.findByNameIgnoreCase(clean).orElseGet(() -> skills.save(new Skill(clean)));
    }

    public Set<Skill> findOrCreateAll(Collection<String> names) {
        Set<Skill> result = new HashSet<>();
        if (names == null) return result;
        for (String n : names) {
            if (n != null && !n.isBlank()) result.add(findOrCreate(n));
        }
        return result;
    }
}
