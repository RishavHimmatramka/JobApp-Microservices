package com.companyms.service;

import java.util.List;

import com.companyms.bean.ReviewSummary;

public interface ReviewClientService {

    List<ReviewSummary> getReviewSummary(Long companyId);

    void deleteReviewsByCompany(Long id);

    boolean isAvailable();

    List<ReviewSummary> getReviewFallback(Long companyId, Throwable throwable);

    void deleteReviewFallback(Long companyId, Throwable throwable);

}
