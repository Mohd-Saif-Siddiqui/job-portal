package com.jobportal.job.controller;

import com.jobportal.job.entity.Skill;
import com.jobportal.job.repository.SkillRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Master list of skills, used by the search filters, the profile editor and the post-job form. */
@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final SkillRepository skills;

    public SkillController(SkillRepository skills) {
        this.skills = skills;
    }

    @GetMapping
    public List<String> all() {
        return skills.findAll(Sort.by("name")).stream().map(Skill::getName).toList();
    }
}
