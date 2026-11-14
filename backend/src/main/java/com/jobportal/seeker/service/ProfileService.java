package com.jobportal.seeker.service;

import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.common.exception.ApiException;
import com.jobportal.common.security.CurrentUser;
import com.jobportal.common.storage.FileStorageService;
import com.jobportal.job.service.SkillService;
import com.jobportal.seeker.dto.ProfileRequest;
import com.jobportal.seeker.dto.ProfileResponse;
import com.jobportal.seeker.dto.SkillEntry;
import com.jobportal.seeker.entity.SeekerProfile;
import com.jobportal.seeker.entity.SeekerSkill;
import com.jobportal.seeker.repository.SeekerProfileRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileService {
    private final CurrentUser currentUser;
    private final UserRepository users;
    private final SeekerProfileRepository profiles;
    private final SkillService skillService;
    private final FileStorageService storage;

    public ProfileService(CurrentUser currentUser, UserRepository users, SeekerProfileRepository profiles,
                          SkillService skillService, FileStorageService storage) {
        this.currentUser = currentUser;
        this.users = users;
        this.profiles = profiles;
        this.skillService = skillService;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public ProfileResponse get() {
        return toResponse(loadProfile());
    }

    @Transactional
    public ProfileResponse update(ProfileRequest req) {
        SeekerProfile p = loadProfile();
        User u = p.getUser();
        u.setFullName(req.fullName().trim());
        users.save(u);
        p.setHeadline(blankToNull(req.headline()));
        p.setLocation(blankToNull(req.location()));
        p.setBio(blankToNull(req.bio()));
        p.setExpectedSalary(req.expectedSalary());

        // replace the skill list: remove the old ones, add the new ones (duplicates ignored)
        p.getSkills().clear();
        profiles.saveAndFlush(p);
        Set<String> seen = new HashSet<>();
        if (req.skills() != null) {
            for (SkillEntry e : req.skills()) {
                if (!seen.add(e.name().trim().toLowerCase())) continue;
                SeekerSkill ss = new SeekerSkill();
                ss.setProfile(p);
                ss.setSkill(skillService.findOrCreate(e.name()));
                ss.setYearsExperience(e.yearsExperience());
                p.getSkills().add(ss);
            }
        }
        profiles.save(p);
        return toResponse(p);
    }

    @Transactional
    public ProfileResponse uploadResume(MultipartFile file) {
        SeekerProfile p = loadProfile();
        String stored = storage.saveResume(file);
        storage.delete(p.getResumeStoredName());
        p.setResumeStoredName(stored);
        p.setResumeOriginalName(file.getOriginalFilename());
        profiles.save(p);
        return toResponse(p);
    }

    @Transactional
    public ProfileResponse deleteResume() {
        SeekerProfile p = loadProfile();
        storage.delete(p.getResumeStoredName());
        p.setResumeStoredName(null);
        p.setResumeOriginalName(null);
        profiles.save(p);
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public String resumeOriginalName() {
        return requireResume(loadProfile()).getResumeOriginalName();
    }

    @Transactional(readOnly = true)
    public Resource resumeFile() {
        return storage.load(requireResume(loadProfile()).getResumeStoredName());
    }

    private SeekerProfile requireResume(SeekerProfile p) {
        if (p.getResumeStoredName() == null) throw ApiException.notFound("No resume uploaded");
        return p;
    }

    private SeekerProfile loadProfile() {
        User u = currentUser.get();
        return profiles.findByUserId(u.getId()).orElseThrow(() -> ApiException.notFound("Profile not found"));
    }

    private ProfileResponse toResponse(SeekerProfile p) {
        List<SkillEntry> skills = p.getSkills().stream()
                .map(s -> new SkillEntry(s.getSkill().getName(), s.getYearsExperience()))
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name())).toList();
        return new ProfileResponse(p.getUser().getFullName(), p.getUser().getEmail(), p.getHeadline(),
                p.getLocation(), p.getBio(), p.getExpectedSalary(), p.getResumeOriginalName(), skills);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
