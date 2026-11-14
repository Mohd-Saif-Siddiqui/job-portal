package com.jobportal.seeker.controller;

import com.jobportal.common.dto.PageResponse;
import com.jobportal.job.dto.JobResponse;
import com.jobportal.job.dto.SearchCriteria;
import com.jobportal.seeker.service.SeekerJobService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seeker/jobs")
public class SeekerJobController {
    private final SeekerJobService service;

    public SeekerJobController(SeekerJobService service) {
        this.service = service;
    }

    /** GET /api/seeker/jobs?q=java&skills=Java,React&location=Pune&remote=true&minSalary=800000&page=0&size=10 */
    @GetMapping
    public PageResponse<JobResponse> search(@RequestParam(required = false) String q,
                                            @RequestParam(required = false) List<String> skills,
                                            @RequestParam(required = false) String location,
                                            @RequestParam(required = false) Boolean remote,
                                            @RequestParam(required = false) Long minSalary,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return service.search(new SearchCriteria(q, skills, location, remote, minSalary), page, size);
    }

    @GetMapping("/saved")
    public List<JobResponse> saved() {
        return service.listSaved();
    }

    @GetMapping("/{id}")
    public JobResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}/save")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void save(@PathVariable Long id) {
        service.save(id);
    }

    @DeleteMapping("/{id}/save")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsave(@PathVariable Long id) {
        service.unsave(id);
    }
}
