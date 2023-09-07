package org.openea.eap.extj.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.entity.VisualConfigEntity;
import org.openea.eap.extj.mapper.VisualConfigMapper;
import org.openea.eap.extj.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.service.VisualConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大屏基本配置
 *
 *
 */
@Service
public class VisualConfigServiceImpl extends SuperServiceImpl<VisualConfigMapper, VisualConfigEntity> implements VisualConfigService {

    @Override
    public List<VisualConfigEntity> getList() {
        QueryWrapper<VisualConfigEntity> queryWrapper = new QueryWrapper<>();
        return this.list(queryWrapper);
    }

    @Override
    public VisualConfigEntity getInfo(String id) {
        QueryWrapper<VisualConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualConfigEntity::getVisualId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualConfigEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualConfigEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualConfigEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}
