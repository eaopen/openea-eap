package org.openea.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.modules.app.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

}
