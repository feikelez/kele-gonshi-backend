package com.kele.gongshibackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批人配置实体类
 *
 * @author kele
 * @since 2026-04-05
 */
@Data
@TableName("approval_config")
@Schema(description = "审批人配置实体")
public class ApprovalConfig {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 审批人ID
     */
    @Schema(description = "审批人ID")
    private Long approverId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
