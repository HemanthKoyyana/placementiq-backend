package com.placementiq.backend.service;

import com.placementiq.backend.model.JobRole;
import com.placementiq.backend.repository.JobRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobRoleService {

    private final JobRoleRepository jobRoleRepository;

    public JobRoleService(JobRoleRepository jobRoleRepository) {
        this.jobRoleRepository = jobRoleRepository;
    }

    public List<JobRole> getJobRoles() {
        return jobRoleRepository.findAll();
    }
}