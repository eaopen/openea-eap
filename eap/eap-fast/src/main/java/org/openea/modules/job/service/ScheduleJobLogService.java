package org.openea.modules.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openea.common.utils.PageUtils;
import org.openea.modules.job.entity.ScheduleJobLogEntity;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface ScheduleJobLogService extends IService<ScheduleJobLogEntity> {

	PageUtils queryPage(Map<String, Object> params);
	
}
