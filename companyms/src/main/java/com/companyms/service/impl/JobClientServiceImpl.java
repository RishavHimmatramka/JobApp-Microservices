package com.companyms.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.companyms.bean.JobSummary;
import com.companyms.client.JobClient;
import com.companyms.service.JobClientService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class JobClientServiceImpl implements JobClientService {

    private JobClient jobClient;
    private static final Logger logger = LoggerFactory.getLogger(JobClientServiceImpl.class);

    public JobClientServiceImpl(JobClient jobClient) {
        this.jobClient = jobClient;
    }

    @Override
    @RateLimiter(name = "jobBreaker")
    @Retry(name = "jobBreaker")
    @CircuitBreaker(name = "jobBreaker", fallbackMethod = "getJobFallback")
    public List<JobSummary> getJobSummary(Long companyId) {
        return jobClient.getJobsByCompany(companyId);
    }

    @Override
    @RateLimiter(name = "jobBreaker")
    @Retry(name = "jobBreaker")
    @CircuitBreaker(name = "jobBreaker", fallbackMethod = "deleteJobFallback")
    public void deleteJobsByCompany(Long companyId) {
        jobClient.deleteJobsByCompany(companyId);
    }

    @Override
    public boolean isAvailable() {
        try {
            String response = jobClient.healthCheck(); 
            return response != null && response.contains("\"status\":\"UP\"");
        } catch (Exception e) {
            // FeignException, Timeout, Connect refused, etc.
            return false;
        }
    }

    @Override
    public List<JobSummary> getJobFallback(Long companyId, Throwable throwable) {
        logger.error("Job service is unavailable. Falling back to empty job list for companyId: {}", companyId, throwable);
        return List.of();
    }

    @Override
    public void deleteJobFallback(Long companyId, Throwable throwable) {
        logger.error("Job service is unavailable. Unable to delete jobs for companyId: {}", companyId, throwable);
        throw new RuntimeException("Job service is currently unavailable. Please try again later.");
    }

}
