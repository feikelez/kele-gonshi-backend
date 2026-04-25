package com.kele.gongshibackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目实体类
 *
 * @author kele
 * @since 2026-04-05
 */
@Data
@TableName("project")
@Schema(description = "项目实体")
public class Project {

    /**
     * 项目ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "项目ID")
    private Long id;

    /**
     * 项目编号
     */
    @Schema(description = "项目编号")
    private String projectCode;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述")
    private String description;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /**
     * 状态 1进行中 2已完成 3已暂停
     */
    @Schema(description = "状态 1进行中 2已完成 3已暂停")
    private Integer status;

    /**
     * 项目经理ID
     */
    @Schema(description = "项目经理ID")
    private Long managerId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 删除标记 0未删除 1已删除
     */
    @TableLogic
    @Schema(description = "删除标记 0未删除 1已删除")
    private Integer delFlag;
}
