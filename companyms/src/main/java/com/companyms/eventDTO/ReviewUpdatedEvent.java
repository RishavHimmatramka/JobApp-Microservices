package com.companyms.eventDTO;

public class ReviewUpdatedEvent {
    private Long id;
    private double oldRating;
    private double newRating;
    private Long companyId;
    public ReviewUpdatedEvent() {
    }
    public ReviewUpdatedEvent(Long id, double oldRating, double newRating, Long companyId) {
        this.id = id;
        this.oldRating = oldRating;
        this.newRating = newRating;
        this.companyId = companyId;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getOldRating() {
        return oldRating;
    }
    public void setOldRating(double oldRating) {
        this.oldRating = oldRating;
    }
    public double getNewRating() {
        return newRating;
    }
    public void setNewRating(double newRating) {
        this.newRating = newRating;
    }
    public Long getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

}
