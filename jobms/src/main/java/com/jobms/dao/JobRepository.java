package com.jobms.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobms.entity.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity,Long>{

    List<JobEntity> findByCompanyId(Long companyId);

}
