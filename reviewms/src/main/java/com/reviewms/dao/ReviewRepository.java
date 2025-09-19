package com.reviewms.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reviewms.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity,Long> {

    List<ReviewEntity> findByCompanyId(Long companyId);

}
