package com.jobms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobms.bean.Job;
import com.jobms.response.JobResponse;
import com.jobms.service.JobService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/jobs")
public class JobController {

    private JobService jobservice;
    public JobController(JobService jobservice) {
        this.jobservice = jobservice;
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs(
            @RequestParam(value = "companyId", required = false) Long companyId) {
        if (companyId != null) {
            return ResponseEntity.ok(jobservice.findByCompanyId(companyId));
        } else {
            return ResponseEntity.ok(jobservice.findAll());
        }
    }

    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody Job job){
        jobservice.createJob(job);
        return new ResponseEntity<>("job added successfully",HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return new ResponseEntity<>(jobservice.getJobById(id),(jobservice.getJobById(id)==null)?HttpStatus.NOT_FOUND:HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id){
        jobservice.deleteJobById(id);
        return new ResponseEntity<>("Successfully deleted",HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteJobsByCompany(@RequestParam(value = "companyId", required = true) Long companyId) {
        jobservice.deleteByCompanyId(companyId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @RequestBody Job updatedjob) {
        return ResponseEntity.ok(jobservice.updateJob(id, updatedjob));
    }

}
