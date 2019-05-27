package org.openea.modules.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openea.common.utils.PageUtils;
import org.openea.modules.oss.entity.SysOssEntity;

import java.util.Map;

/**
 * 文件上传
 *
 */
public interface SysOssService extends IService<SysOssEntity> {

	PageUtils queryPage(Map<String, Object> params);
}
