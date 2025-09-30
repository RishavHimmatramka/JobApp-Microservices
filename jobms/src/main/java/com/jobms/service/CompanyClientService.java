package com.jobms.service;

import org.springframework.http.ResponseEntity;

import com.jobms.bean.CompanySummary;

public interface CompanyClientService {

    ResponseEntity<CompanySummary> getCompanySummary(Long companyId);

	ResponseEntity<CompanySummary> companyFallback(Long companyId, Throwable throwable);

}
