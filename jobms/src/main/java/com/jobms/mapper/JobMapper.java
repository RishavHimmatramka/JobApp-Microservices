package com.jobms.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.jobms.bean.Job;
import com.jobms.entity.JobEntity;

@Mapper(componentModel = "spring"
    ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobMapper {
    JobEntity toEntity(Job job);
    Job toBean(JobEntity jobEntity);
    List<Job> toBeanList(List<JobEntity> jobEntities);
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "companyId",ignore = true)
    void updateEntityFromBean(Job job,@MappingTarget JobEntity jobEntity);
}
