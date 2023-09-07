package org.openea.eap.extj.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.EapUserProvider;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.base.entity.InterfaceOauthEntity;
import org.openea.eap.extj.base.mapper.InterfaceOauthMapper;
import org.openea.eap.extj.base.model.InterfaceOauth.PaginationOauth;
import org.openea.eap.extj.base.service.InterfaceOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterfaceOauthServiceImpl extends SuperServiceImpl<InterfaceOauthMapper, InterfaceOauthEntity> implements InterfaceOauthService {

    @Autowired
    private EapUserProvider userProvider;


    @Override
    public boolean isExistByAppName(String appName, String id) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(InterfaceOauthEntity::getAppName, appName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(InterfaceOauthEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByAppId(String appId, String id) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(InterfaceOauthEntity::getAppId, appId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(InterfaceOauthEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public List<InterfaceOauthEntity> getList(PaginationOauth pagination) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();

        //查询关键字
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(InterfaceOauthEntity::getAppId, pagination.getKeyword())
                            .or().like(InterfaceOauthEntity::getAppName, pagination.getKeyword())
            );
        }
        //排序
        queryWrapper.lambda().orderByAsc(InterfaceOauthEntity::getSortCode)
                .orderByDesc(InterfaceOauthEntity::getCreatorTime);
        Page<InterfaceOauthEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<InterfaceOauthEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), iPage.getTotal());
    }

    @Override
    public InterfaceOauthEntity getInfo(String id) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(InterfaceOauthEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(InterfaceOauthEntity entity) {
        if (entity.getId() == null) {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
            entity.setCreatorTime(DateUtil.getNowDate());
        }
        this.save(entity);
    }

    @Override
    public boolean update(InterfaceOauthEntity entity, String id) throws DataException {
        entity.setId(id);
        entity.setLastModifyUserId(userProvider.get().getUserId());
        entity.setLastModifyTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(InterfaceOauthEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public InterfaceOauthEntity getInfoByAppId(String appId) {
        QueryWrapper<InterfaceOauthEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(InterfaceOauthEntity::getAppId, appId);
        return this.getOne(queryWrapper);
    }
}
