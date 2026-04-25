package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.SysConfig;
import com.kele.gongshibackend.mapper.SysConfigMapper;
import com.kele.gongshibackend.service.SysConfigService;
import org.springframework.stereotype.Service;

/**
 * 系统配置服务实现类
 *
 * @author kele
 * @since 2026-04-05
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {
}
