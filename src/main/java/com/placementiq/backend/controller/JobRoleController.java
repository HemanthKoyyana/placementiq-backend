package com.placementiq.backend.controller;

import com.placementiq.backend.model.JobRole;
import com.placementiq.backend.service.JobRoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class JobRoleController {

    private final JobRoleService jobRoleService;

    public JobRoleController(JobRoleService jobRoleService) {
        this.jobRoleService = jobRoleService;
    }

  
   @GetMapping("/api/job-roles")
public List<JobRole> getJobRoles() {
    return jobRoleService.getJobRoles();
}
}
