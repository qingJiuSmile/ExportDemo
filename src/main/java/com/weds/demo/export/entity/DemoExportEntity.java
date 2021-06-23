package com.weds.demo.export.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class DemoExportEntity {
    @Excel(name = "记录人userId")
    private Integer userId;
    @Excel(name = "序列")
    private String devSerial;
    @Excel(name = "记录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Timestamp recordTime;
}
