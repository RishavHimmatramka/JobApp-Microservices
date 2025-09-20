package com.companyms.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.companyms.bean.ReviewSummary;

@FeignClient(name="REVIEWMS")
public interface ReviewClient {

    @GetMapping("/reviews")
    List<ReviewSummary> getReviewsByCompany(@RequestParam(value = "companyId", required = true) Long companyId);

    @DeleteMapping("/reviews")
    void deleteReviewsByCompany(@RequestParam(value = "companyId", required = true) Long companyId);
}
