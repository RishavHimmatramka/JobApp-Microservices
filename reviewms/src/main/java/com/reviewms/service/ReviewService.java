package com.reviewms.service;

import java.util.List;

import com.reviewms.bean.Review;

public interface ReviewService {

    List<Review> findAll(Long companyId);
    void addReview(Long companyId, Review review);
    Review findById(Long reviewId);
    void updateReview(Long reviewId, Review review);
    void deleteReviewById(Long reviewId);
    void deleteByCompanyId(Long companyId);
}
