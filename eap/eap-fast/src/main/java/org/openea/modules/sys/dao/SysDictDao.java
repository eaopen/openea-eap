package org.openea.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.openea.modules.sys.entity.SysDictEntity;

/**
 * 数据字典
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysDictDao extends BaseMapper<SysDictEntity> {
	
}
