package com.jobms.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.jobms.bean.CompanySummary;
import com.jobms.bean.Job;
import com.jobms.client.CompanyClient;
import com.jobms.dao.JobRepository;
import com.jobms.entity.JobEntity;
import com.jobms.exception.JobNotFoundException;
import com.jobms.mapper.JobMapper;
import com.jobms.response.JobResponse;
import com.jobms.service.JobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JobServiceImpl implements JobService {

    private JobRepository jobRepository;
    private JobMapper jobMapper;
    private CompanyClient companyClient;
    private ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper, CompanyClient companyClient, ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.companyClient = companyClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<JobResponse> findAll() {
        List<JobEntity> jobEntities = jobRepository.findAll();
        logger.info("Fetched {} jobs from repository", jobEntities.size());
        return jobEntities.stream().map(this::toJobResponse).toList();
    }

    @Override
    public void createJob(Job job) {
        job.setId(null);
        if (job.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID is required to create a job");
        }
        Long companyId = job.getCompanyId();
        if (!validateCompany(companyId)) {
            throw new JobNotFoundException("Cannot create job. Company with ID " + companyId + " does not exist.");
        }
        JobEntity jobEntity = jobMapper.toEntity(job);
        jobRepository.save(jobEntity);
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
            throw new JobNotFoundException("Cannot update job. Company with ID " + companyId + " does not exist.");
        }
        return toJobResponse(jobRepository.save(existingJob));
    }

    @Override
    public boolean validateCompany(Long companyId) {
        return getCompanySummary(companyId).getStatusCode().is2xxSuccessful();
    }

    @Override
    public ResponseEntity<CompanySummary> getCompanySummary(Long companyId) {
        try {
            ResponseEntity<JsonNode> response = companyClient.getCompanySummary(companyId);
            JsonNode node = response.getBody();
            CompanySummary companySummary = objectMapper.treeToValue(node.get("company"), CompanySummary.class);
            return new ResponseEntity<>(companySummary, response.getStatusCode());
        } catch (IllegalArgumentException | JsonProcessingException ex) {
            throw new RuntimeException("Error calling Company service: " + ex.getMessage(), ex);
        }
    }

    @Override
    public JobResponse toJobResponse(JobEntity jobEntity) {
        Job job = jobMapper.toBean(jobEntity);
        CompanySummary companySummary = getCompanySummary(job.getCompanyId()).getBody();
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
