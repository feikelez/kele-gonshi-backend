package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.TokenBlacklist;
import com.kele.gongshibackend.mapper.TokenBlacklistMapper;
import com.kele.gongshibackend.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class TokenBlacklistServiceImpl 
        extends ServiceImpl<TokenBlacklistMapper, TokenBlacklist> 
        implements TokenBlacklistService {
    
    @Override
    public boolean addToBlacklist(String token, Long userId) {
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setToken(token);
        blacklist.setUserId(userId);
        blacklist.setExpireTime(LocalDateTime.now().plusDays(1)); // 默认1天过期
        return save(blacklist);
    }
    
    @Override
    public boolean isInBlacklist(String token) {
        QueryWrapper<TokenBlacklist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        queryWrapper.gt("expire_time", LocalDateTime.now());
        return count(queryWrapper) > 0;
    }
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点清理过期Token
    @Override
    public void cleanExpiredTokens() {
        QueryWrapper<TokenBlacklist> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("expire_time", LocalDateTime.now());
        remove(queryWrapper);
        log.info("已清理过期的Token黑名单记录");
    }
}
