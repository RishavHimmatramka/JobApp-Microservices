package com.companyms.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.companyms.bean.Company;
import com.companyms.bean.JobSummary;
import com.companyms.bean.ReviewSummary;
import com.companyms.dao.CompanyRepository;
import com.companyms.entity.CompanyEntity;
import com.companyms.exception.CompanyNotFoundException;
import com.companyms.mapper.CompanyMapper;
import com.companyms.response.CompanyResponse;
import com.companyms.service.CompanyService;
import com.companyms.service.JobClientService;
import com.companyms.service.ReviewClientService;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository;
    private CompanyMapper companyMapper;
    private JobClientService jobClientService;
    private ReviewClientService reviewClientService;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper,
            JobClientService jobClientService, ReviewClientService reviewClientService) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
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
        if (!jobClientService.isAvailable()) {
            throw new RuntimeException("Job service is not available. Aborting deletion.");
        }
        if (!reviewClientService.isAvailable()) {
            throw new RuntimeException("Review service is not available. Aborting deletion.");
        }
        jobClientService.deleteJobsByCompany(id);
        reviewClientService.deleteReviewsByCompany(id);
        companyRepository.deleteById(id);
    }

    @Override
    public CompanyResponse toCompanyResponse(CompanyEntity entity) {
        Company company = companyMapper.toBean(entity);
        List<JobSummary> jobResponse = jobClientService.getJobSummary(company.getId());
        List<ReviewSummary> reviewResponse = reviewClientService.getReviewSummary(company.getId());
        return new CompanyResponse(company, jobResponse, reviewResponse);
    }

}
