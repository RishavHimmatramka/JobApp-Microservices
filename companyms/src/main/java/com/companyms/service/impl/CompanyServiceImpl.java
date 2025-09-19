package com.companyms.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.companyms.bean.Company;
import com.companyms.bean.JobSummary;
import com.companyms.bean.ReviewSummary;
import com.companyms.dao.CompanyRepository;
import com.companyms.entity.CompanyEntity;
import com.companyms.exception.CompanyNotFoundException;
import com.companyms.mapper.CompanyMapper;
import com.companyms.response.CompanyResponse;
import com.companyms.service.CompanyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository;
    private CompanyMapper companyMapper;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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
        String jobServiceUrl = "http://localhost:8082/jobs?companyId=" + id;
        restTemplate.delete(jobServiceUrl);
        String reviewServiceUrl = "http://localhost:8083/reviews?companyId=" + id;
        restTemplate.delete(reviewServiceUrl);
    }

    @Override
    public List<JobSummary> getJobSummary(Long companyId) {
        String jobServiceUrl = "http://JOBMS:8082/jobs?companyId=" + companyId;
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                jobServiceUrl,
                HttpMethod.GET,
                null,
                JsonNode.class,
                companyId
            );
            List<JobSummary> jobs = new ArrayList<>();
            for (JsonNode node : response.getBody()) {
                JobSummary job = objectMapper.treeToValue(node.get("job"), JobSummary.class);
                jobs.add(job);
            }
            return jobs;
        } catch (RestClientException | JsonProcessingException | IllegalArgumentException ex) {
            throw new RuntimeException("Error calling Job service: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<ReviewSummary> getReviewSummary(Long companyId) {
        String reviewServiceUrl = "http://REVIEWMS:8083/reviews?companyId=" + companyId;
        try {
            ResponseEntity<ReviewSummary[]> response = restTemplate.exchange(
                reviewServiceUrl,
                HttpMethod.GET,
                null,
                ReviewSummary[].class
            );
            List<ReviewSummary> reviews = Arrays.asList(response.getBody());
            return reviews;
        } catch (RestClientException | IllegalArgumentException ex) {
            throw new RuntimeException("Error calling Review service: " + ex.getMessage(), ex);
        }
    }

    @Override
    public CompanyResponse toCompanyResponse(CompanyEntity entity) {
        Company company = companyMapper.toBean(entity);
        List<JobSummary> jobResponse = getJobSummary(company.getId());
        List<ReviewSummary> reviewResponse = getReviewSummary(company.getId());
        return new CompanyResponse(company, jobResponse, reviewResponse);
    }

    
}
