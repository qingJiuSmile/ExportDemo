package com.weds.demo.export.mapper;

import com.weds.demo.export.entity.DemoExportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface BmDevRecordMapper {
    List<DemoExportEntity> testGetBigData(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize);

    int  testGetBigDataCount();
}