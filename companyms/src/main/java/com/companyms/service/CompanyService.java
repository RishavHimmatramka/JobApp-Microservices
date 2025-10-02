package com.companyms.service;

import java.util.List;

import com.companyms.bean.Company;
import com.companyms.entity.CompanyEntity;
import com.companyms.eventDTO.ReviewCreatedEvent;
import com.companyms.eventDTO.ReviewDeletedEvent;
import com.companyms.eventDTO.ReviewUpdatedEvent;
import com.companyms.response.CompanyResponse;

public interface CompanyService {

    List<Company> findAll();
    Company createCompany(Company company);
    CompanyResponse getCompanyById(Long id);
    boolean updateCompany(Long id, Company updatedCompany);
    CompanyResponse toCompanyResponse(CompanyEntity entity);
    void deleteCompanyById(Long id);
    void updateCompanyRatingOnCreate(ReviewCreatedEvent event);
    void updateCompanyRatingOnUpdate(ReviewUpdatedEvent event);
    void updateCompanyRatingOnDelete(ReviewDeletedEvent event);
}
