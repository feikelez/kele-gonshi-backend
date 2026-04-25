package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.WorkRecord;
import com.kele.gongshibackend.mapper.WorkRecordMapper;
import com.kele.gongshibackend.service.WorkRecordService;
import org.springframework.stereotype.Service;

/**
 * 工时记录服务实现类
 *
 * @author kele
 * @since 2026-04-05
 */
@Service
public class WorkRecordServiceImpl extends ServiceImpl<WorkRecordMapper, WorkRecord> implements WorkRecordService {
}
