package com.kele.gongshibackend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 每日工时统计 VO
 *
 * @author kele
 * @since 2026-04-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "每日工时统计响应")
public class DailyWorkHoursVO {

    /**
     * 星期标签，如"周一"、"周二"
     */
    @Schema(description = "星期标签")
    private String label;

    /**
     * 当日工时（小时），保留两位小数
     */
    @Schema(description = "当日工时（小时）")
    private BigDecimal hours;
}