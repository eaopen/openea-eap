package org.openea.eap.extj.base.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.entity.DictionaryTypeEntity;
import org.openea.eap.extj.base.mapper.DictionaryTypeMapper;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.DictionaryTypeService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.util.EapUserProvider;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictionaryTypeServiceImpl extends SuperServiceImpl<DictionaryTypeMapper, DictionaryTypeEntity> implements DictionaryTypeService {

    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private DictionaryDataService dictionaryDataService;

    @Override
    public List<DictionaryTypeEntity> getList() {
        QueryWrapper<DictionaryTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(DictionaryTypeEntity::getSortCode)
                .orderByDesc(DictionaryTypeEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public DictionaryTypeEntity getInfoByEnCode(String enCode) {
        QueryWrapper<DictionaryTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryTypeEntity::getEnCode, enCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public DictionaryTypeEntity getInfo(String id) {
        QueryWrapper<DictionaryTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryTypeEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<DictionaryTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryTypeEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DictionaryTypeEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<DictionaryTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryTypeEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DictionaryTypeEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void create(DictionaryTypeEntity entity) {
        //判断id是否为空,为空则为新建
        if (StringUtil.isEmpty(entity.getId())){
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
        }
        this.save(entity);
    }

    @Override
    public boolean update(String id, DictionaryTypeEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public boolean delete(DictionaryTypeEntity entity) {
        List<DictionaryTypeEntity> dictionaryTypeEntityList = list().stream().filter(t -> entity.getId().equals(t.getParentId())).collect(Collectors.toList());
        //没有子分类的时候才能删
        if (dictionaryTypeEntityList.size() == 0) {
            if (dictionaryDataService.getList(entity.getId()).size() == 0){
                this.removeById(entity.getId());
                return true;
            }
        }
        return false;
    }

    @Override
    @DSTransactional
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        DictionaryTypeEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<DictionaryTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .lt(DictionaryTypeEntity::getSortCode, upSortCode)
                .eq(DictionaryTypeEntity::getParentId, upEntity.getParentId())
                .orderByDesc(DictionaryTypeEntity::getSortCode);
        List<DictionaryTypeEntity> downEntity = this.list(queryWrapper);
        if (downEntity.size() > 0) {
            //交换两条记录的sort值
            Long temp = upEntity.getSortCode();
            upEntity.setSortCode(downEntity.get(0).getSortCode());
            downEntity.get(0).setSortCode(temp);
            this.updateById(downEntity.get(0));
            this.updateById(upEntity);
            isOk = true;
        }
        return isOk;
    }

    @Override
    @DSTransactional
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        DictionaryTypeEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<DictionaryTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(DictionaryTypeEntity::getSortCode, upSortCode)
                .eq(DictionaryTypeEntity::getParentId, downEntity.getParentId())
                .orderByAsc(DictionaryTypeEntity::getSortCode);
        List<DictionaryTypeEntity> upEntity = this.list(queryWrapper);
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
}
