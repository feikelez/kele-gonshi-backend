package com.kele.gongshibackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_token_blacklist")
public class TokenBlacklist implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String token;
    
    private Long userId;
    
    private LocalDateTime expireTime;
    
    private LocalDateTime createTime;
}
