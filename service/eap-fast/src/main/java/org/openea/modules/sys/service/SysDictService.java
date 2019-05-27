package org.openea.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openea.common.utils.PageUtils;
import org.openea.modules.sys.entity.SysDictEntity;

import java.util.Map;

/**
 * 数据字典
 *
 */
public interface SysDictService extends IService<SysDictEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

