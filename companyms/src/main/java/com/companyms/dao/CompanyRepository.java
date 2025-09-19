package com.companyms.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.companyms.entity.CompanyEntity;

public interface CompanyRepository extends JpaRepository<CompanyEntity,Long> {

}
