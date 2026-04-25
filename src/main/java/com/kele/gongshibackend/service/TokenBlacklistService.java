package com.kele.gongshibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kele.gongshibackend.entity.TokenBlacklist;

public interface TokenBlacklistService extends IService<TokenBlacklist> {
    
    boolean addToBlacklist(String token, Long userId);
    
    boolean isInBlacklist(String token);
    
    void cleanExpiredTokens();
}
