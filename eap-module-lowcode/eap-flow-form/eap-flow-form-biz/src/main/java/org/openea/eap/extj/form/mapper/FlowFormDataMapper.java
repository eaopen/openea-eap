package org.openea.eap.extj.form.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.dynamic.sql.util.mybatis3.*;

/**
 * mybatis3 表单mapper对象
 *
 *
 */
@Mapper
public interface FlowFormDataMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper, CommonSelectMapper,
		CommonUpdateMapper {
}
