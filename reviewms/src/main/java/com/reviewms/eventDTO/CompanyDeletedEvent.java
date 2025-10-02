package com.reviewms.eventDTO;

public class CompanyDeletedEvent {
    private Long companyId;

    public CompanyDeletedEvent() {
    }

    public CompanyDeletedEvent(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

}
