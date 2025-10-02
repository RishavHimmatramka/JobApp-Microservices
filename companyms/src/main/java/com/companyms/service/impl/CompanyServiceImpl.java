package com.companyms.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.companyms.bean.Company;
import com.companyms.bean.JobSummary;
import com.companyms.bean.ReviewSummary;
import com.companyms.dao.CompanyRepository;
import com.companyms.entity.CompanyEntity;
import com.companyms.eventDTO.ReviewCreatedEvent;
import com.companyms.eventDTO.ReviewDeletedEvent;
import com.companyms.eventDTO.ReviewUpdatedEvent;
import com.companyms.exception.CompanyNotFoundException;
import com.companyms.mapper.CompanyMapper;
import com.companyms.messaging.CompanyEventPublisher;
import com.companyms.response.CompanyResponse;
import com.companyms.service.CompanyService;
import com.companyms.service.JobClientService;
import com.companyms.service.ReviewClientService;

import jakarta.transaction.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository;
    private CompanyMapper companyMapper;
    private CompanyEventPublisher companyEventPublisher;
    private JobClientService jobClientService;
    private ReviewClientService reviewClientService;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper,
            CompanyEventPublisher companyEventPublisher, JobClientService jobClientService, ReviewClientService reviewClientService) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.companyEventPublisher = companyEventPublisher;
        this.jobClientService = jobClientService;
        this.reviewClientService = reviewClientService;
    }

    @Override
    public List<Company> findAll(){
        return companyMapper.toBeanList(companyRepository.findAll());
    }

    @Override
    public Company createCompany(Company company) {
        company.setId(null);
        CompanyEntity companyEntity = companyMapper.toEntity(company);
        return companyMapper.toBean(companyRepository.save(companyEntity));
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        CompanyEntity companyEntity = companyRepository.findById(id)
            .orElseThrow(() -> new CompanyNotFoundException("Company with ID " + id + " not found."));
        return toCompanyResponse(companyEntity);
    }

    @Override
    public boolean updateCompany(Long id, Company updatedCompany) {
        Optional<CompanyEntity> optionalCompanyEntity = companyRepository.findById(id);
        if(optionalCompanyEntity.isPresent()){
            CompanyEntity existingEntity = optionalCompanyEntity.get();
            companyMapper.updateEntityFromBean(updatedCompany, existingEntity);
            companyRepository.save(existingEntity);
            return true;
        }
        else
            throw new CompanyNotFoundException("Company with ID " + id + " not found.");
    }

    @Override
    public void deleteCompanyById(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new CompanyNotFoundException("Company with ID " + id + " not found.");
        }
        companyRepository.deleteById(id);
        companyEventPublisher.publishCompanyDeletedEvent(id);
    }

    @Override
    public CompanyResponse toCompanyResponse(CompanyEntity entity) {
        Company company = companyMapper.toBean(entity);
        List<JobSummary> jobResponse = jobClientService.getJobSummary(company.getId());
        List<ReviewSummary> reviewResponse = reviewClientService.getReviewSummary(company.getId());
        return new CompanyResponse(company, jobResponse, reviewResponse);
    }

    @Override
    @Transactional
    public void updateCompanyRatingOnCreate(ReviewCreatedEvent event) {
        // Long companyId = event.getCompanyId();
        // CompanyEntity companyEntity = companyRepository.findById(companyId)
        //     .orElseThrow(() -> new CompanyNotFoundException("Company with ID " + companyId + " not found."));
        // double avg = companyEntity.getAverageRating();
        // int count = companyEntity.getReviewCount();
        // double newAvg = (avg * count + event.getRating()) / (count + 1);
        // companyEntity.setAverageRating(newAvg);
        // companyEntity.setReviewCount(count + 1);
        // companyRepository.save(companyEntity);
        companyRepository.updateRatingAtomically(event.getCompanyId(), event.getRating(), 1);
    }

    @Override
    @Transactional
    public void updateCompanyRatingOnUpdate(ReviewUpdatedEvent event) {
        // Long companyId = event.getCompanyId();
        // CompanyEntity companyEntity = companyRepository.findById(companyId)
        //     .orElseThrow(() -> new CompanyNotFoundException("Company with ID " + companyId + " not found."));
        // double avg = companyEntity.getAverageRating();
        // int count = companyEntity.getReviewCount();
        // double newAvg = (avg * count - event.getOldRating() + event.getNewRating()) / count;
        // companyEntity.setAverageRating(newAvg);
        // companyRepository.save(companyEntity);
        companyRepository.updateRatingAtomically(event.getCompanyId(), event.getNewRating() - event.getOldRating(), 0);
    }

    @Override
    @Transactional
    public void updateCompanyRatingOnDelete(ReviewDeletedEvent event) {
        // Long companyId = event.getCompanyId();
        // CompanyEntity companyEntity = companyRepository.findById(companyId)
        //     .orElseThrow(() -> new CompanyNotFoundException("Company with ID " + companyId + " not found."));
        // double avg = companyEntity.getAverageRating();
        // int count = companyEntity.getReviewCount();
        // if (count <= 1) {
        //     companyEntity.setAverageRating(0);
        //     companyEntity.setReviewCount(0);
        // } else {
        //     double newAvg = (avg * count - event.getRating()) / (count - 1);
        //     companyEntity.setAverageRating(newAvg);
        //     companyEntity.setReviewCount(count - 1);
        // }
        // companyRepository.save(companyEntity);
        companyRepository.updateRatingAtomically(event.getCompanyId(), -event.getRating(), -1);
    }
    
}
