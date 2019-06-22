package org.openea.eapboot.modules.base.service.mybatis;

import org.openea.eapboot.modules.base.entity.Role;
import org.openea.eapboot.modules.base.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 */
@CacheConfig(cacheNames = "userRole")
public interface IUserRoleService extends IService<UserRole> {

    /**
     * 通过用户id获取
     * @param userId
     * @return
     */
    @Cacheable(key = "#userId")
    List<Role> findByUserId(String userId);

    /**
     * 通过用户id获取用户角色关联的部门数据
     * @param userId
     * @return
     */
    @Cacheable(key = "'depIds:'+#userId")
    List<String> findDepIdsByUserId(String userId);
}
