package com.companyms.bean;

public class Company {
    private Long id;
    private String name;
    private String description;
    private double averageRating;
    private int reviewCount;

    public Company() {
    }

    public Company(Long id, String name, String description, double averageRating, int reviewCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public Long getId() {
        return id;
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
    public double getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    public int getReviewCount() {
        return reviewCount;
    }
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
