package com.weds.demo.export.service;

import com.weds.demo.export.entity.DemoExportEntity;
import com.weds.demo.export.mapper.BmDevRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BmDevRecordService {
    @Autowired
    private BmDevRecordMapper bmDevRecordMapper;

    public List<DemoExportEntity> testGetBigData(Integer pageIndex, Integer pageSize){
        return bmDevRecordMapper.testGetBigData(pageIndex,pageSize);
    }

    public int testGetBigDataCount(){

        return bmDevRecordMapper.testGetBigDataCount();
    }
}
