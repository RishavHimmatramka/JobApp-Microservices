package com.companyms.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double averageRating;
    private int reviewCount;
    private double ratingSum;

    public CompanyEntity() {
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getId() {
        return id;
    }
    public CompanyEntity(Long id, String name, String description, double averageRating, int reviewCount, double ratingSum) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.ratingSum = ratingSum;
    }
    public int getReviewCount() {
        return reviewCount;
    }
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public double getRatingSum() {
        return ratingSum;
    }
    public void setRatingSum(double ratingSum) {
        this.ratingSum = ratingSum;
    }
}
