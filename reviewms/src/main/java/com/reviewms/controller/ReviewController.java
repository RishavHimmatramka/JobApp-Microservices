package com.reviewms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reviewms.bean.Review;
import com.reviewms.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam(value = "companyId",required = true) Long companyId){
        return new ResponseEntity<>(reviewService.findAll(companyId),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addReview(@RequestParam(value = "companyId",required = true) Long companyId,@RequestBody Review review) {
        reviewService.addReview(companyId, review);
        return new ResponseEntity<>("Review added successfully", HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteReviewsByCompanyId(@RequestParam(value = "companyId",required = true) Long companyId){
        reviewService.deleteByCompanyId(companyId);
        return new ResponseEntity<>("Reviews for company with id " + companyId + " deleted successfully.",HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long reviewId) {
        Review review = reviewService.findById(reviewId);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId, @RequestBody Review review) {
        reviewService.updateReview(reviewId, review);
        return new ResponseEntity<>("Review updated successfully.", HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId){
        reviewService.deleteReviewById(reviewId);
        return new ResponseEntity<>("Review deleted successfully.",HttpStatus.OK);
    }
}
