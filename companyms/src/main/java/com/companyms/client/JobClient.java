package com.companyms.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.companyms.bean.JobSummary;

@FeignClient(name="JOBMS")
public interface JobClient {

    @GetMapping("/jobs")
    List<JobSummary> getJobsByCompany(@RequestParam(value = "companyId", required = true) Long companyId);

    @DeleteMapping("/jobs")
    void deleteJobsByCompany(@RequestParam(value = "companyId", required = true) Long companyId);
}
