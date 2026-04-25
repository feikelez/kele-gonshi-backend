package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.ApprovalConfig;
import com.kele.gongshibackend.mapper.ApprovalConfigMapper;
import com.kele.gongshibackend.service.ApprovalConfigService;
import org.springframework.stereotype.Service;

/**
 * 审批人配置服务实现类
 *
 * @author kele
 * @since 2026-04-05
 */
@Service
public class ApprovalConfigServiceImpl extends ServiceImpl<ApprovalConfigMapper, ApprovalConfig> implements ApprovalConfigService {
}
