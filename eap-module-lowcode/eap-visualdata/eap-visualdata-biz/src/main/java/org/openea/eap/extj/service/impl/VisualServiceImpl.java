package org.openea.eap.extj.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.entity.VisualConfigEntity;
import org.openea.eap.extj.entity.VisualEntity;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.mapper.VisualMapper;
import org.openea.eap.extj.model.visual.VisualPaginationModel;
import org.openea.eap.extj.service.VisualConfigService;
import org.openea.eap.extj.service.VisualService;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.UserProvider;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 大屏基本信息
 *
 *
 */
@Service
public class VisualServiceImpl extends SuperServiceImpl<VisualMapper, VisualEntity> implements VisualService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private VisualConfigService configService;

    @Override
    public List<VisualEntity> getList(VisualPaginationModel pagination) {
        QueryWrapper<VisualEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualEntity::getCategory, pagination.getCategory());
        queryWrapper.lambda().orderByDesc(VisualEntity::getCreateTime);
        Page page = new Page(pagination.getCurrent(), pagination.getSize());
        IPage<VisualEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public List<VisualEntity> getList() {
        QueryWrapper<VisualEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(VisualEntity::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public VisualEntity getInfo(String id) {
        QueryWrapper<VisualEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualEntity entity, VisualConfigEntity configEntity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreateTime(new Date());
        entity.setUpdateUser(userProvider.get().getUserId());
        entity.setStatus(1);
        entity.setIsDeleted(0);
        this.save(entity);
        configEntity.setVisualId(entity.getId());
        configService.create(configEntity);
    }

    @Override
    public boolean update(String id, VisualEntity entity, VisualConfigEntity configEntity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userProvider.get().getUserId());
        boolean flag = this.updateById(entity);
        if (configEntity != null) {
            configService.update(configEntity.getId(), configEntity);
        }
        return flag;
    }

    @Override
    public void delete(VisualEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
            VisualConfigEntity config = configService.getInfo(entity.getId());
            configService.delete(config);
        }
    }

    @Override
    public void createImport(VisualEntity entity, VisualConfigEntity configEntity) throws DataException {
        try {
            this.save(entity);
            configService.create(configEntity);
        }catch (Exception e){
            throw new DataException(MsgCode.IMP003.get());
        }

    }
}
