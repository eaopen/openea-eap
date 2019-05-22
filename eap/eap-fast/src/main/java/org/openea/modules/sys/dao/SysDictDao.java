package org.openea.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openea.modules.sys.entity.SysDictEntity;

/**
 * 数据字典
 *
 */
@Mapper
public interface SysDictDao extends BaseMapper<SysDictEntity> {
	
}
