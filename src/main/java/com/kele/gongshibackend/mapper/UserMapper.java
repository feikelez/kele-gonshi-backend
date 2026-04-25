package com.kele.gongshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kele.gongshibackend.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author kele
 * @since 2026-04-05
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
