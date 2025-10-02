package com.jobms.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jobms.bean.Job;
import com.jobms.entity.JobEntity;
import com.jobms.response.JobResponse;

public interface JobService {

    List<JobResponse> findAll();
    ResponseEntity<String> createJob(Job job);
    JobResponse getJobById(Long id);
    void deleteJobById(Long id);
    JobResponse updateJob(Long id, Job updatedjob);
    public JobResponse toJobResponse(JobEntity jobEntity);
    boolean validateCompany(Long companyId);
    List<Job> findByCompanyId(Long companyId);
    void deleteByCompanyId(Long companyId);
}
