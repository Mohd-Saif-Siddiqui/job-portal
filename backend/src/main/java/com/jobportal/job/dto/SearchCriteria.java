package com.jobportal.job.dto;

import java.util.List;

/** What the seeker typed into the search filters. Every field is optional. */
public record SearchCriteria(String q, List<String> skills, String location, Boolean remote, Long minSalary) {
}
