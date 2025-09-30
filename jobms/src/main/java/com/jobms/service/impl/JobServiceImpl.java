package com.jobms.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.jobms.bean.CompanySummary;
import com.jobms.bean.Job;
import com.jobms.dao.JobRepository;
import com.jobms.entity.JobEntity;
import com.jobms.exception.JobNotFoundException;
import com.jobms.mapper.JobMapper;
import com.jobms.response.JobResponse;
import com.jobms.service.CompanyClientService;
import com.jobms.service.JobService;

@Service
public class JobServiceImpl implements JobService {

    private JobRepository jobRepository;
    private JobMapper jobMapper;
    private CompanyClientService companyClientService;
    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper, CompanyClientService companyClientService) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.companyClientService = companyClientService;
    }

    @Override
    public List<JobResponse> findAll() {
        List<JobEntity> jobEntities = jobRepository.findAll();
        logger.info("Fetched {} jobs from repository", jobEntities.size());
        return jobEntities.stream().map(this::toJobResponse).toList();
    }

    @Override
    public ResponseEntity<String> createJob(Job job) {
        job.setId(null);
        if (job.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID is required to create a job");
        }
        Long companyId = job.getCompanyId();
        if (!validateCompany(companyId)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Cannot create job: Company service is unavailable.");
        }
        JobEntity jobEntity = jobMapper.toEntity(job);
        jobRepository.save(jobEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("Job created successfully with ID: " + jobEntity.getId());
    }

    @Override
    public JobResponse getJobById(Long id) {
        JobEntity entity = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
        return toJobResponse(entity);
    }

    @Override
    public void deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new JobNotFoundException("Job with ID " + id + " does not exist.");
        }
    }

    @Override
    public JobResponse updateJob(Long id, Job updatedjob) {
        JobEntity existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
        Long companyId = updatedjob.getCompanyId();
        jobMapper.updateEntityFromBean(updatedjob, existingJob);
        if (companyId != null && validateCompany(companyId)) {
            existingJob.setCompanyId(companyId);
        } else if (companyId != null && !validateCompany(companyId)) {
            throw new IllegalArgumentException("Cannot update job: Company service is unavailable.");
        }
        return toJobResponse(jobRepository.save(existingJob));
    }

    @Override
    public boolean validateCompany(Long companyId) {
        return companyClientService.getCompanySummary(companyId).getStatusCode().is2xxSuccessful();
    }

    @Override
    public JobResponse toJobResponse(JobEntity jobEntity) {
        Job job = jobMapper.toBean(jobEntity);
        CompanySummary companySummary = companyClientService.getCompanySummary(job.getCompanyId()).getBody();
        logger.debug("Response from Company MS: {}", companySummary.getId());
        return new JobResponse(job, companySummary);
    }

    @Override
    public List<Job> findByCompanyId(Long companyId) {
        List<JobEntity> jobEntities = jobRepository.findByCompanyId(companyId);
        logger.info("Fetched {} jobs for company ID {} from repository", jobEntities.size(), companyId);
        return jobMapper.toBeanList(jobEntities);
    }

    @Override
    public void deleteByCompanyId(Long companyId) {
        List<JobEntity> jobs = jobRepository.findByCompanyId(companyId);
        if (jobs.isEmpty()) {
            return; // no jobs to delete
        }
        jobRepository.deleteAll(jobs);
    }

}
