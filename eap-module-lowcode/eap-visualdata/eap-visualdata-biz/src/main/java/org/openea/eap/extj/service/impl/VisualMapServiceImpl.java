package org.openea.eap.extj.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.entity.VisualMapEntity;
import org.openea.eap.extj.mapper.VisualMapMapper;
import org.openea.eap.extj.service.VisualMapService;
import org.openea.eap.extj.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.model.VisualPagination;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大屏地图配置
 *
 *
 */
@Service
public class VisualMapServiceImpl extends SuperServiceImpl<VisualMapMapper, VisualMapEntity> implements VisualMapService {

    @Override
    public List<VisualMapEntity> getList(VisualPagination pagination) {
        return getListWithColnums(pagination);
    }


    @Override
    public List<VisualMapEntity> getListWithColnums(VisualPagination pagination, SFunction<VisualMapEntity, ?>... columns) {
        QueryWrapper<VisualMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(columns).
                orderByAsc(VisualMapEntity::getId);
        Page page = new Page(pagination.getCurrent(), pagination.getSize());
        IPage<VisualMapEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public VisualMapEntity getInfo(String id) {
        QueryWrapper<VisualMapEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualMapEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualMapEntity entity) {
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualMapEntity entity) {
        entity.setId(id);
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualMapEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }
}
