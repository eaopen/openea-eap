package org.openea.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.openea.common.utils.PageUtils;
import org.openea.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

}
