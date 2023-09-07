package org.openea.eap.extj.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.JdbcUtil;
import org.openea.eap.extj.entity.VisualDbEntity;
import org.openea.eap.extj.mapper.VisualDbMapper;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.UserProvider;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.model.VisualPagination;
import org.openea.eap.extj.service.VisualDbService;
import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 大屏数据源配置
 *
 *
 */
@Service
public class VisualDbServiceImpl extends SuperServiceImpl<VisualDbMapper, VisualDbEntity> implements VisualDbService {

    @Autowired
    private UserProvider userProvider;

    @Override
    public List<VisualDbEntity> getList(VisualPagination pagination) {
        QueryWrapper<VisualDbEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(VisualDbEntity::getCreateTime);
        Page page = new Page(pagination.getCurrent(), pagination.getSize());
        IPage<VisualDbEntity> iPages = this.page(page, queryWrapper);
        return pagination.setData(iPages);
    }

    @Override
    public List<VisualDbEntity> getList() {
        QueryWrapper<VisualDbEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(VisualDbEntity::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public VisualDbEntity getInfo(String id) {
        QueryWrapper<VisualDbEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(VisualDbEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(VisualDbEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreateTime(new Date());
        entity.setUpdateUser(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, VisualDbEntity entity) {
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public void delete(VisualDbEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public boolean dbTest(VisualDbEntity entity) {
        boolean flag = false;
        try {
            @Cleanup Connection conn = ConnUtil.getConn(entity.getUsername(), entity.getPassword(), entity.getUrl());
            flag = conn!=null;
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Map<String,Object>> query(VisualDbEntity entity, String sql) {
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = JdbcUtil.queryList(new PrepSqlDTO(sql).withConn(entity.getUsername(), entity.getPassword(), entity.getUrl())).setIsLowerCase(true).get();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return data;
    }

}
