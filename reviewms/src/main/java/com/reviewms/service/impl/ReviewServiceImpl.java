package com.reviewms.service.impl;

import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.reviewms.bean.CompanySummary;
import com.reviewms.bean.Review;
import com.reviewms.dao.ReviewRepository;
import com.reviewms.entity.ReviewEntity;
import com.reviewms.exception.ReviewNotFoundException;
import com.reviewms.mapper.ReviewMapper;
import com.reviewms.service.ReviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Review> findAll(Long companyId) {
        List<ReviewEntity> reviewEntities = reviewRepository.findByCompanyId(companyId);
        return reviewMapper.toBeanList(reviewEntities);
    }

    @Override
    public void addReview(Long companyId, Review review) {
        review.setId(null);
        review.setCompanyId(companyId);
        if(!validateCompany(companyId)){
            throw new RuntimeException("Company with id " + companyId + " does not exist.");
        }
        ReviewEntity reviewEntity = reviewMapper.toEntity(review);
        reviewRepository.save(reviewEntity);
    }

    @Override
    public Review findById(Long reviewId) {
        return reviewMapper.toBean(
            reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review with ID " + reviewId + " not found"))
        );
    }

    @Override
    public void updateReview(Long reviewId, Review review) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));
        reviewMapper.updateEntityFromBean(review, reviewEntity);
        reviewRepository.save(reviewEntity);
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        if(!reviewRepository.existsById(reviewId)){
            throw new ReviewNotFoundException("Review with ID " + reviewId + " not found");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public void deleteByCompanyId(Long companyId) {
        List<ReviewEntity> reviews = reviewRepository.findByCompanyId(companyId);
        if (reviews.isEmpty()) {
            return; // no reviews to delete
        }
        reviewRepository.deleteAll(reviews);
    }

    public boolean validateCompany(Long companyId){
        return getCompanySummary(companyId).getStatusCode().is2xxSuccessful();
    }

    private ResponseEntity<CompanySummary> getCompanySummary(Long companyId) {
        String COMPANY_SERVICE_URL = "http://COMPANYMS:8081/companies/" + companyId;
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    COMPANY_SERVICE_URL,
                    HttpMethod.GET,
                    null,
                    JsonNode.class
                );
            JsonNode node = response.getBody();
            CompanySummary companySummary = objectMapper.treeToValue(node.get("company"), CompanySummary.class);
            return new ResponseEntity<>(companySummary, response.getStatusCode());
        } catch (RestClientException | JsonProcessingException | IllegalArgumentException ex) {
            throw new RuntimeException("Error calling Company service: " + ex.getMessage(), ex);
        }
    }
}