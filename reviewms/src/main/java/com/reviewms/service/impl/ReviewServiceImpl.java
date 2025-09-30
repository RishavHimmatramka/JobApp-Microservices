package com.reviewms.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.reviewms.bean.Review;
import com.reviewms.dao.ReviewRepository;
import com.reviewms.entity.ReviewEntity;
import com.reviewms.exception.ReviewNotFoundException;
import com.reviewms.mapper.ReviewMapper;
import com.reviewms.service.CompanyClientService;
import com.reviewms.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private CompanyClientService companyClientService;
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper, CompanyClientService companyClientService) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.companyClientService = companyClientService;
    }

    @Override
    public List<Review> findAll(Long companyId) {
        List<ReviewEntity> reviewEntities = reviewRepository.findByCompanyId(companyId);
        return reviewMapper.toBeanList(reviewEntities);
    }

    @Override
    public ResponseEntity<String> addReview(Long companyId, Review review) {
        review.setId(null);
        review.setCompanyId(companyId);
        if(!validateCompany(companyId)){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Cannot create review: Company service is unavailable.");
        }
        ReviewEntity reviewEntity = reviewMapper.toEntity(review);
        reviewRepository.save(reviewEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review created successfully with ID: " + reviewEntity.getId());
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

    @Override
    public boolean validateCompany(Long companyId){
        return companyClientService.getCompanySummary(companyId).getStatusCode().is2xxSuccessful();
    }

    
}