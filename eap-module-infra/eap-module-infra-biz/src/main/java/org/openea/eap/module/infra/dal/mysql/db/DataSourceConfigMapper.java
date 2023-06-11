package org.openea.eap.module.infra.dal.mysql.db;

import org.openea.eap.framework.mybatis.core.mapper.BaseMapperX;
import org.openea.eap.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
