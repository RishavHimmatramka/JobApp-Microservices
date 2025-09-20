package com.jobms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.databind.JsonNode;

@FeignClient(name="COMPANYMS")
public interface CompanyClient {

    @GetMapping("/companies/{companyId}")
    ResponseEntity<JsonNode> getCompanySummary(@PathVariable Long companyId);
}
