package com.kele.gongshibackend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Top项目工时统计响应")
public class TopProjectsStatsVO {

    @Schema(description = "年月，格式 YYYY-MM")
    private String month;

    @Schema(description = "项目工时列表，按 totalHours 降序排列")
    private List<ProjectHours> projects;

    @Data
    public static class ProjectHours {
        @Schema(description = "项目ID")
        private Long projectId;

        @Schema(description = "项目名称")
        private String projectName;

        @Schema(description = "该月累计工时")
        private BigDecimal totalHours;

        @Schema(description = "环比增长率（%），0 表示无变化或新项目")
        private Integer trend;
    }
}
