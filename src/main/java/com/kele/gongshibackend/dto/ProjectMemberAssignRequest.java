package com.kele.gongshibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 项目成员分配请求
 *
 * @author kele
 * @since 2026-04-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "项目成员分配请求")
public class ProjectMemberAssignRequest {

    @Schema(description = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @Schema(description = "用户ID列表")
    @NotNull(message = "用户ID列表不能为空")
    private List<Long> userIds;
}
