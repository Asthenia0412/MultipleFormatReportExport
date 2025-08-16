package io.github.asthenia0412.multipleformatreportexport.mapper;


import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CodeAnalysisMapper {

    List<CodeAnalysis> findByIssueType(@Param("issueType") String issueType,
                                       @Param("offset") int offset,
                                       @Param("pageSize") int pageSize);


    List<CodeAnalysis> findAllWithPagination(@Param("offset") int offset,
                                             @Param("pageSize") int pageSize);


    int countAll();
}