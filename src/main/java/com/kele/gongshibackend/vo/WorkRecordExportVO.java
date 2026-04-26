package com.kele.gongshibackend.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 工时记录导出 VO
 */
@Data
public class WorkRecordExportVO {

    @ExcelProperty("填报人")
    private String userName;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("任务名称")
    private String taskName;

    @ExcelProperty("工作内容")
    private String workContent;

    @ExcelProperty("工作时长")
    private BigDecimal hours;

    @ExcelProperty("上报日期")
    private LocalDate workDate;
}
