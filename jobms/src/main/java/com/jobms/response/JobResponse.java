package com.jobms.response;

import com.jobms.bean.CompanySummary;
import com.jobms.bean.Job;

public class JobResponse {
    private Job job;
    private CompanySummary company;
    public JobResponse() {
    }
    public JobResponse(Job job, CompanySummary company) {
        this.job = job;
        this.company = company;
    }
    public Job getJob() {
        return job;
    }
    public void setJob(Job job) {
        this.job = job;
    }
    public CompanySummary getCompanySummary() {
        return company;
    }
    public void setCompanySummary(CompanySummary company) {
        this.company = company;
    }
}
