package com.companyms.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.companyms.entity.CompanyEntity;

public interface CompanyRepository extends JpaRepository<CompanyEntity,Long> {

    @Modifying
    @Query("""
        UPDATE CompanyEntity c 
        SET c.ratingSum = c.ratingSum + :deltaSum, 
        c.reviewCount = c.reviewCount + :deltaCount, 
        c.averageRating = CASE WHEN (c.reviewCount + :deltaCount) = 0 THEN 0 
                          ELSE (c.ratingSum + :deltaSum) / (c.reviewCount + :deltaCount) 
                     END
        WHERE c.id = :companyId
    """)
    int updateRatingAtomically(@Param("companyId") Long companyId,
        @Param("deltaSum") double deltaSum,
        @Param("deltaCount") int deltaCount);

}
