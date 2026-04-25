package com.kele.gongshibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kele.gongshibackend.entity.Task;
import com.kele.gongshibackend.mapper.TaskMapper;
import com.kele.gongshibackend.service.TaskService;
import org.springframework.stereotype.Service;

/**
 * 任务服务实现类
 *
 * @author kele
 * @since 2026-04-05
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
}
