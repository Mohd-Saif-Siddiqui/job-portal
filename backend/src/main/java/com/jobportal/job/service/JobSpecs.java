package com.jobportal.job.service;

import com.jobportal.job.dto.SearchCriteria;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.entity.Skill;
import jakarta.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/** Builds the database query for job search from the filters the seeker picked. */
public final class JobSpecs {
    private JobSpecs() {}

    public static Specification<Job> search(SearchCriteria c, Instant postedAfter) {
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(root.get("status"), JobStatus.OPEN));

            if (hasText(c.q())) {
                String like = "%" + c.q().trim().toLowerCase() + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("companyName")), like),
                        cb.like(cb.lower(root.get("description")), like)));
            }
            if (hasText(c.location())) {
                ps.add(cb.like(cb.lower(root.get("location")), "%" + c.location().trim().toLowerCase() + "%"));
            }
            if (Boolean.TRUE.equals(c.remote())) {
                ps.add(cb.isTrue(root.get("remote")));
            }
            if (c.minSalary() != null && c.minSalary() > 0) {
                // show jobs whose top salary reaches what the seeker wants
                ps.add(cb.greaterThanOrEqualTo(root.get("salaryMax"), c.minSalary()));
            }
            if (c.skills() != null && !c.skills().isEmpty()) {
                List<String> names = c.skills().stream().filter(JobSpecs::hasText)
                        .map(s -> s.trim().toLowerCase()).toList();
                if (!names.isEmpty()) {
                    // jobs that need at least ONE of the chosen skills (subquery avoids duplicate rows)
                    Subquery<Long> sq = query.subquery(Long.class);
                    Root<Job> sj = sq.correlate(root);
                    Join<Job, Skill> sk = sj.join("skills");
                    sq.select(sj.get("id")).where(cb.lower(sk.get("name")).in(names));
                    ps.add(cb.exists(sq));
                }
            }
            if (postedAfter != null) {
                ps.add(cb.greaterThan(root.get("postedAt"), postedAfter));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
