package com.kele.gongshibackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工时记录实体类
 *
 * @author kele
 * @since 2026-04-05
 */
@Data
@TableName("work_record")
@Schema(description = "工时记录实体")
public class WorkRecord {

    /**
     * 工时记录ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "工时记录ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private Long projectId;

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    private Long taskId;

    /**
     * 工作日期
     */
    @Schema(description = "工作日期")
    private LocalDate workDate;

    /**
     * 工作类型 1日报 2周报
     */
    @Schema(description = "工作类型 1日报 2周报")
    private Integer workType;

    /**
     * 工时数
     */
    @Schema(description = "工时数")
    private BigDecimal hours;

    /**
     * 工作内容
     */
    @Schema(description = "工作内容")
    private String workContent;

    /**
     * 状态 1待审批 2已通过 3已驳回
     */
    @Schema(description = "状态 1待审批 2已通过 3已驳回")
    private Integer status;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    /**
     * 审批时间
     */
    @Schema(description = "审批时间")
    private LocalDateTime approvalTime;

    /**
     * 审批人ID
     */
    @Schema(description = "审批人ID")
    private Long approverId;

    /**
     * 审批意见
     */
    @Schema(description = "审批意见")
    private String approvalComment;

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
