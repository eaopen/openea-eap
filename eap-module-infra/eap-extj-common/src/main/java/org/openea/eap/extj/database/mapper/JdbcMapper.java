package org.openea.eap.extj.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface JdbcMapper extends BaseMapper<Object> {
    List<Map<String, Object>> getList(@Param("sql") String sql);
}
