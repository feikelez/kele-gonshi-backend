package com.kele.gongshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kele.gongshibackend.entity.TokenBlacklist;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenBlacklistMapper extends BaseMapper<TokenBlacklist> {
}
