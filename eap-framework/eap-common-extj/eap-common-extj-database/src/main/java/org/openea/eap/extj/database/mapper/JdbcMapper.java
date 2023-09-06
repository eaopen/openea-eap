package org.openea.eap.extj.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 类功能
 *
 */
@Mapper
public interface JdbcMapper extends BaseMapper<Object> {

    void jdbcInsert(@Param("sql")String sql);

    void jdbcDelete(@Param("sql")String sql);

    void jdbcUpdate(@Param("sql")String sql);

    List<Map<String, Object>> jdbcSelect(@Param("sql")String sql);

}
