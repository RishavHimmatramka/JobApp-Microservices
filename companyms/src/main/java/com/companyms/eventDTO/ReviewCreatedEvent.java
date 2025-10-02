package com.companyms.eventDTO;

public class ReviewCreatedEvent {
    private Long id;
    private double rating;
    private Long companyId;
    public ReviewCreatedEvent() {
    }
    public ReviewCreatedEvent(Long id, double rating, Long companyId) {
        this.id = id;
        this.rating = rating;
        this.companyId = companyId;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public Long getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

}
