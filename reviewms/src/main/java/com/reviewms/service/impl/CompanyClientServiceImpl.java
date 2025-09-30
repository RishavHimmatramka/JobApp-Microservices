package com.reviewms.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewms.bean.CompanySummary;
import com.reviewms.client.CompanyClient;
import com.reviewms.service.CompanyClientService;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class CompanyClientServiceImpl implements CompanyClientService {

    private CompanyClient companyClient;
    private ObjectMapper objectMapper;

    public CompanyClientServiceImpl(CompanyClient companyClient, ObjectMapper objectMapper) {
        this.companyClient = companyClient;
        this.objectMapper = objectMapper;
    }

    @Override
    @RateLimiter(name = "companyBreaker")
    @Retry(name = "companyBreaker")
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "companyFallback")
    public ResponseEntity<CompanySummary> getCompanySummary(Long companyId) {
        try {
            ResponseEntity<JsonNode> response = companyClient.getCompanySummary(companyId);
            JsonNode node = response.getBody();
            CompanySummary companySummary = objectMapper.treeToValue(node.get("company"), CompanySummary.class);
            return new ResponseEntity<>(companySummary, response.getStatusCode());
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Error processing Json: " + ex.getMessage(), ex);
        }
    }

    @Override
    public ResponseEntity<CompanySummary> companyFallback(Long companyId, Throwable throwable) {
        if(throwable instanceof IllegalArgumentException) {
            throw (IllegalArgumentException) throwable;
        }
        else if(throwable instanceof FeignException.NotFound) {
            throw (FeignException.NotFound) throwable;
        }
        CompanySummary fallbackSummary = new CompanySummary();
        fallbackSummary.setId(companyId);
        fallbackSummary.setName("Unknown");
        fallbackSummary.setDescription("Company service is currently unavailable.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(fallbackSummary);
    }
}
