package com.reviewms.service;

import org.springframework.http.ResponseEntity;

import com.reviewms.bean.CompanySummary;

public interface CompanyClientService {

    ResponseEntity<CompanySummary> getCompanySummary(Long companyId);

    ResponseEntity<CompanySummary> companyFallback(Long companyId, Throwable throwable);

}
