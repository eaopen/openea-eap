package org.openea.eap.extj.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.util.EapUserProvider;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.base.entity.ComFieldsEntity;
import org.openea.eap.extj.base.mapper.BaseComFieldsMapper;
import org.openea.eap.extj.base.service.ComFieldsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ComFieldsServiceImpl extends SuperServiceImpl<BaseComFieldsMapper, ComFieldsEntity> implements ComFieldsService {

    @Autowired
    private EapUserProvider userProvider;


    @Override
    public List<ComFieldsEntity> getList() {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(ComFieldsEntity::getSortcode).orderByDesc(ComFieldsEntity::getCreatortime);
        return this.list(queryWrapper);
    }

    @Override
    public ComFieldsEntity getInfo(String id) {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ComFieldsEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<ComFieldsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ComFieldsEntity::getFieldName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ComFieldsEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }



    @Override
    public void create(ComFieldsEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatortime(new Date());
        entity.setCreatoruserid(userProvider.get().getUserId());
        entity.setEnabledmark(1);
        this.save(entity);
    }

    @Override
    public boolean update(String id, ComFieldsEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastmodifyuserid(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(ComFieldsEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

}