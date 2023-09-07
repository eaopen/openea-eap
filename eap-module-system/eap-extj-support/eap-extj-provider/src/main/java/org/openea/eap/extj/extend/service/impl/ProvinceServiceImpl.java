package org.openea.eap.extj.extend.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.Page;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.util.EapUserProvider;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.extend.entity.ProvinceEntity;
import org.openea.eap.extj.extend.mapper.ProvinceMapper;
import org.openea.eap.extj.extend.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ProvinceServiceImpl extends SuperServiceImpl<ProvinceMapper, ProvinceEntity> implements ProvinceService {

    @Autowired
    private EapUserProvider userProvider;

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ProvinceEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(ProvinceEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public List<ProvinceEntity> getList(String parentId) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getParentId, parentId);
        // 排序
        queryWrapper.lambda().orderByDesc(ProvinceEntity::getCreatorTime);
        return this.list(queryWrapper);
    }


    @Override
    public List<ProvinceEntity> getList(String parentId, Page page) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        // 模糊查询
        if (Objects.nonNull(page) && StringUtil.isNotEmpty(page.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t->t.like(ProvinceEntity::getFullName, page.getKeyword()).or()
                            .like(ProvinceEntity::getEnCode, page.getKeyword())
            );
        }
        queryWrapper.lambda().eq(ProvinceEntity::getParentId, parentId);
        // 排序
        queryWrapper.lambda().orderByAsc(ProvinceEntity::getSortCode).orderByDesc(ProvinceEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(ProvinceEntity::getLastModifyTime);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<ProvinceEntity> getAllList() {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(ProvinceEntity::getSortCode).orderByAsc(ProvinceEntity::getCreatorTime);

        return this.list(queryWrapper);
    }

    @Override
    public List<ProvinceEntity> getProListBytype(String type) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(ProvinceEntity::getId,ProvinceEntity::getFullName).eq(ProvinceEntity::getType,type);
        return this.list(queryWrapper);
    }

    @Override
    public List<ProvinceEntity> getProList(List<String> ProIdList) {
        if (ProIdList.size()>0){
            QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().select(ProvinceEntity::getId,ProvinceEntity::getFullName).in(ProvinceEntity::getId,ProIdList);
            return this.list(queryWrapper);
        }
        return new ArrayList<>();
    }


    @Override
    public ProvinceEntity getInfo(String id) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public ProvinceEntity getInfo(String fullName,List<String> parentId) {
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceEntity::getFullName, fullName);
        if (parentId.size() > 0) {
            queryWrapper.lambda().in(ProvinceEntity::getParentId, parentId);
        }
        return this.getOne(queryWrapper);
    }


    @Override
    public void delete(ProvinceEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(ProvinceEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public boolean update(String id, ProvinceEntity entity) {

        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    @DSTransactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        ProvinceEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(ProvinceEntity::getSortCode, upSortCode)
                .eq(ProvinceEntity::getParentId, upEntity.getParentId())
                .orderByDesc(ProvinceEntity::getSortCode);
        List<ProvinceEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            updateById(downEntity.get(0));
            updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @DSTransactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        ProvinceEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(ProvinceEntity::getSortCode, upSortCode)
                .eq(ProvinceEntity::getParentId, downEntity.getParentId())
                .orderByAsc(ProvinceEntity::getSortCode);
        List<ProvinceEntity> upEntity = this.list(queryWrapper);
        if (upEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = downEntity.getSortCode();
            downEntity.setSortCode(upEntity.get(0).getSortCode());
            upEntity.get(0).setSortCode(temp);
            updateById(upEntity.get(0));
            updateById(downEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    public List<ProvinceEntity> infoList(List<String> lists) {
        List<ProvinceEntity> list =new ArrayList<>();
        if(!ObjectUtils.isEmpty(lists)){
            QueryWrapper<ProvinceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(ProvinceEntity::getId, lists);
            list = this.list(queryWrapper);
        }
        return list;

    }
}