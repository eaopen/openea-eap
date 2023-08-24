package org.openea.eap.module.visualdev.extend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.dynamic.sql.util.mybatis3.*;

@Mapper
public interface FlowFormDataMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper, CommonSelectMapper,
        CommonUpdateMapper {
}
