package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.permission.entity.ColumnsPurviewEntity;
import org.openea.eap.extj.permission.mapper.ColumnsPurviewMapper;
import org.openea.eap.extj.permission.service.ColumnsPurviewService;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 模块列表权限业务实现类
 *
 */
@Service
public class ColumnsPurviewServiceImpl extends SuperServiceImpl<ColumnsPurviewMapper, ColumnsPurviewEntity> implements ColumnsPurviewService {
    @Autowired
    private UserProvider userProvider;

    @Override
    public ColumnsPurviewEntity getInfo(String moduleId) {
        QueryWrapper<ColumnsPurviewEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ColumnsPurviewEntity::getModuleId, moduleId);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean update(String moduleId, ColumnsPurviewEntity entity) {
        ColumnsPurviewEntity entitys = getInfo(moduleId);
        // id不存在则是保存
        if (entitys == null) {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
            return this.save(entity);
        } else {
            // 修改
            entity.setId(entitys.getId());
            entity.setLastModifyUserId(userProvider.get().getUserId());
            entity.setLastModifyTime(DateUtil.getNowDate());
        }
        return this.saveOrUpdate(entity);
    }

}
