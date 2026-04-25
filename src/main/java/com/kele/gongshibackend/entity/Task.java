package com.kele.gongshibackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务实体类
 *
 * @author kele
 * @since 2026-04-05
 */
@Data
@TableName("task")
@Schema(description = "任务实体")
public class Task {

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "任务ID")
    private Long id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private Long projectId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;

    /**
     * 任务描述
     */
    @Schema(description = "任务描述")
    private String description;

    /**
     * 指派人ID
     */
    @Schema(description = "指派人ID")
    private Long assigneeId;

    /**
     * 状态 1待处理 2进行中 3已完成
     */
    @Schema(description = "状态 1待处理 2进行中 3已完成")
    private Integer status;

    /**
     * 计划工时
     */
    @Schema(description = "计划工时")
    private BigDecimal planHours;

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
