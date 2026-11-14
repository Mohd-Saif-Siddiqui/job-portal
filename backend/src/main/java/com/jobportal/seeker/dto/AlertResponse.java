package com.jobportal.seeker.dto;

import java.time.Instant;
import java.util.List;

public record AlertResponse(Long id, String name, String q, List<String> skills, String location, boolean remote,
                            Long minSalary, long newCount, Instant createdAt) {
}
