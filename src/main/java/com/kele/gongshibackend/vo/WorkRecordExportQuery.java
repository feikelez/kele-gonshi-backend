package com.kele.gongshibackend.vo;

import lombok.Data;

/**
 * 工时记录导出查询条件
 */
@Data
public class WorkRecordExportQuery {

    private Long userId;

    private Long projectId;

    private String startDate;

    private String endDate;

    public WorkRecordExportQuery() {
    }

    public WorkRecordExportQuery(Long userId, Long projectId, String startDate, String endDate) {
        this.userId = userId;
        this.projectId = projectId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
