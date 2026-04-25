package com.kele.gongshibackend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "项目按周工时统计响应")
public class ProjectWeeklyStatsVO {

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "年月，格式 YYYY-MM")
    private String month;

    @Schema(description = "周工时列表")
    private List<WeekHours> weeks;

    @Data
    public static class WeekHours {
        @Schema(description = "周次，1-4")
        private Integer week;

        @Schema(description = "工时数")
        private BigDecimal hours;
    }
}
