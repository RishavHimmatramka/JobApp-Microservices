package com.companyms.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.companyms.bean.Company;
import com.companyms.entity.CompanyEntity;

@Mapper(componentModel = "spring"
    ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyMapper {
    Company toBean(CompanyEntity companyEntity);
    @Mapping(target = "ratingSum",ignore = true)
    CompanyEntity toEntity(Company company);
    List<Company> toBeanList(List<CompanyEntity> companyEntities);
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "ratingSum",ignore = true)
    void updateEntityFromBean(Company company, @MappingTarget CompanyEntity companyEntity);
}
