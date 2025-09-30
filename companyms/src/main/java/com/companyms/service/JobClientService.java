package com.companyms.service;

import java.util.List;

import com.companyms.bean.JobSummary;

public interface JobClientService {

    List<JobSummary> getJobSummary(Long companyId);

    void deleteJobsByCompany(Long id);

    boolean isAvailable();

    List<JobSummary> getJobFallback(Long companyId, Throwable throwable);

    void deleteJobFallback(Long companyId, Throwable throwable);

}
