package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.User;
import com.kele.gongshibackend.mapper.UserMapper;
import com.kele.gongshibackend.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @author kele
 * @since 2026-04-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
