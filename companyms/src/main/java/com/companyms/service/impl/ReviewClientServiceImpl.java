package com.companyms.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.companyms.bean.ReviewSummary;
import com.companyms.client.ReviewClient;
import com.companyms.service.ReviewClientService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class ReviewClientServiceImpl implements ReviewClientService {

    private ReviewClient reviewClient;
    private static final Logger logger = LoggerFactory.getLogger(ReviewClientServiceImpl.class);
    
    public ReviewClientServiceImpl(ReviewClient reviewClient) {
        this.reviewClient = reviewClient;
    }

    @Override
    @RateLimiter(name = "reviewBreaker")
    @Retry(name = "reviewBreaker")
    @CircuitBreaker(name = "reviewBreaker", fallbackMethod = "getReviewFallback")
    public List<ReviewSummary> getReviewSummary(Long companyId) {
        return reviewClient.getReviewsByCompany(companyId);
    }

    @Override
    @RateLimiter(name = "reviewBreaker")
    @Retry(name = "reviewBreaker")
    @CircuitBreaker(name = "reviewBreaker", fallbackMethod = "deleteReviewFallback")
    public void deleteReviewsByCompany(Long companyId) {
        reviewClient.deleteReviewsByCompany(companyId);
    }

    @Override
    public boolean isAvailable() {
        try {
            String response = reviewClient.healthCheck(); 
            return response != null && response.contains("\"status\":\"UP\"");
        } catch (Exception e) {
            // FeignException, Timeout, Connect refused, etc.
            return false;
        }
    }


    @Override
    public List<ReviewSummary> getReviewFallback(Long companyId, Throwable throwable) {
        return List.of();
    }

    @Override
    public void deleteReviewFallback(Long companyId, Throwable throwable) {
        logger.error("Review service is unavailable. Unable to delete reviews for companyId: {}", companyId, throwable);
        throw new RuntimeException("Review service is currently unavailable. Please try again later.");
    }
}
