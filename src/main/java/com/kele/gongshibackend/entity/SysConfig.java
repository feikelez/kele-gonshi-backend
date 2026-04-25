package com.kele.gongshibackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 *
 * @author kele
 * @since 2026-04-05
 */
@Data
@TableName("sys_config")
@Schema(description = "系统配置实体")
public class SysConfig {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private Long id;

    /**
     * 配置键
     */
    @Schema(description = "配置键")
    private String configKey;

    /**
     * 配置值
     */
    @Schema(description = "配置值")
    private String configValue;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

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
}
