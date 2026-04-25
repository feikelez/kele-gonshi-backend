package com.kele.gongshibackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "修改密码请求")
public class PasswordChangeRequest {
    
    @Schema(description = "原密码")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
