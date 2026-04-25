package com.kele.gongshibackend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户VO类
 *
 * @author kele
 * @since 2026-04-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户VO")
public class UserVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;
}