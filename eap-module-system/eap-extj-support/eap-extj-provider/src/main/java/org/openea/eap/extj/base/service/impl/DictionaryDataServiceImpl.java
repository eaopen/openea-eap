package org.openea.eap.extj.base.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.DictionaryTypeEntity;
import org.openea.eap.extj.base.mapper.DictionaryDataMapper;
import org.openea.eap.extj.base.model.DictionaryDataExportModel;
import org.openea.eap.extj.base.model.DictionaryExportModel;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.DictionaryTypeService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;
import org.openea.eap.extj.util.file.FileExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DictionaryDataServiceImpl extends SuperServiceImpl<DictionaryDataMapper, DictionaryDataEntity> implements DictionaryDataService {

    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Override
    public List<DictionaryDataEntity> getList(String dictionaryTypeId, Boolean enable) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId);
        if (enable) {
            queryWrapper.lambda().eq(DictionaryDataEntity::getEnabledMark, 1);
        }
        queryWrapper.lambda().orderByAsc(DictionaryDataEntity::getSortCode).orderByDesc(DictionaryDataEntity::getCreatorTime).orderByDesc(DictionaryDataEntity::getLastModifyTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DictionaryDataEntity> getList(String dictionaryTypeId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(
                t -> t.eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId)
                        .or().eq(DictionaryDataEntity::getEnCode, dictionaryTypeId)
        );
        queryWrapper.lambda().orderByAsc(DictionaryDataEntity::getSortCode)
                .orderByDesc(DictionaryDataEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DictionaryDataEntity> getDicList(String dictionaryTypeId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(
                t -> t.eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId)
                        .or().eq(DictionaryDataEntity::getEnCode, dictionaryTypeId)
        );
        queryWrapper.lambda().select(DictionaryDataEntity::getId, DictionaryDataEntity::getFullName, DictionaryDataEntity::getEnCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<DictionaryDataEntity> geDicList(String dictionaryTypeId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().and(
                t -> t.eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId)
                        .or().eq(DictionaryDataEntity::getEnCode, dictionaryTypeId)
        );
        queryWrapper.lambda().select(DictionaryDataEntity::getId, DictionaryDataEntity::getFullName);
        return this.list(queryWrapper);
    }

    @Override
    public Boolean isExistSubset(String parentId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getParentId, parentId);
        return this.list(queryWrapper).size() > 0;
    }

    @Override
    public DictionaryDataEntity getInfo(String id) {
        if (id == null) {
            return null;
        }
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public DictionaryDataEntity getSwapInfo(String value, String dictionaryTypeId) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId).and(
                t -> t.eq(DictionaryDataEntity::getId, value)
                        .or().eq(DictionaryDataEntity::getEnCode, value)
        );
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistByFullName(String dictionaryTypeId, String fullName, String id) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getFullName, fullName).eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DictionaryDataEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String dictionaryTypeId, String enCode, String id) {
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DictionaryDataEntity::getEnCode, enCode).eq(DictionaryDataEntity::getDictionaryTypeId, dictionaryTypeId);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DictionaryDataEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public void delete(DictionaryDataEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public void create(DictionaryDataEntity entity) {
        //判断id是否为空,为空则为新建
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
            entity.setSimpleSpelling(PinYinUtil.getFirstSpell(entity.getFullName()).toUpperCase());
            entity.setCreatorUserId(userProvider.get().getUserId());
        }
        this.save(entity);
    }

    @Override
    public boolean update(String id, DictionaryDataEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(DateUtil.getNowDate());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    public boolean first(String id) {
        boolean isOk = false;
        //获取要上移的那条数据的信息
        DictionaryDataEntity upEntity = this.getById(id);
        Long upSortCode = upEntity.getSortCode() == null ? 0 : upEntity.getSortCode();
        //查询上几条记录
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DictionaryDataEntity::getDictionaryTypeId, upEntity.getDictionaryTypeId())
                .eq(DictionaryDataEntity::getParentId, upEntity.getParentId())
                .lt(DictionaryDataEntity::getSortCode, upSortCode)
                .orderByDesc(DictionaryDataEntity::getSortCode);
        List<DictionaryDataEntity> downEntity = this.list(queryWrapper);
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
    public boolean next(String id) {
        boolean isOk = false;
        //获取要下移的那条数据的信息
        DictionaryDataEntity downEntity = this.getById(id);
        Long upSortCode = downEntity.getSortCode() == null ? 0 : downEntity.getSortCode();
        //查询下几条记录
        QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DictionaryDataEntity::getDictionaryTypeId, downEntity.getDictionaryTypeId())
                .eq(DictionaryDataEntity::getParentId, downEntity.getParentId())
                .gt(DictionaryDataEntity::getSortCode, upSortCode)
                .orderByAsc(DictionaryDataEntity::getSortCode);
        List<DictionaryDataEntity> upEntity = this.list(queryWrapper);
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
    public List<DictionaryDataEntity> getDictionName(List<String> id) {
        List<DictionaryDataEntity> dictionList = new ArrayList<>();
        if (id != null && id.size() > 0) {
            QueryWrapper<DictionaryDataEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().and(
                    t -> t.in(DictionaryDataEntity::getEnCode, id)
                            .or().in(DictionaryDataEntity::getId, id)
            );
            queryWrapper.lambda().orderByAsc(DictionaryDataEntity::getParentId);
            dictionList = this.list(queryWrapper);
        }
        return dictionList;
    }

    @Override
    public DownloadVO exportData(String id) {
        //获取数据分类字段详情
        DictionaryTypeEntity typeEntity = dictionaryTypeService.getInfo(id);
        DictionaryExportModel exportModel = new DictionaryExportModel();
        //递归子分类
        Set<DictionaryTypeEntity> set = new HashSet<>();
        List<DictionaryTypeEntity> typeEntityList = dictionaryTypeService.getList();
        getDictionaryTypeEntitySet(typeEntity, set, typeEntityList);
        List<DictionaryTypeEntity> collect = new ArrayList<>(set);
        //判断是否有子分类
        if (collect.size() > 0) {
            exportModel.setList(collect);
        }
        //判断是否需要new
        if (exportModel.getList() == null) {
            List<DictionaryTypeEntity> list = new ArrayList<>();
            list.add(typeEntity);
            exportModel.setList(list);
        } else {
            exportModel.getList().add(typeEntity);
        }
        //获取该类型下的数据
        List<DictionaryDataExportModel> modelList = new ArrayList<>();
        for (DictionaryTypeEntity dictionaryTypeEntity : exportModel.getList()) {
            List<DictionaryDataEntity> entityList = getList(dictionaryTypeEntity.getId());
            for (DictionaryDataEntity dictionaryDataEntity : entityList) {
                DictionaryDataExportModel dataExportModel = JsonUtil.getJsonToBean(dictionaryDataEntity, DictionaryDataExportModel.class);
                modelList.add(dataExportModel);
            }
        }
        exportModel.setModelList(modelList);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(exportModel, configValueUtil.getTemporaryFilePath(), typeEntity.getFullName(), ModuleTypeEnum.SYSTEM_DICTIONARYDATA.getTableName());
        return downloadVO;
    }

    /**
     * 递归字典分类
     *
     * @param dictionaryTypeEntity 数据字典类型实体
     */
    private void getDictionaryTypeEntitySet(DictionaryTypeEntity dictionaryTypeEntity, Set<DictionaryTypeEntity> set, List<DictionaryTypeEntity> typeEntityList) {
        //是否含有子分类
        List<DictionaryTypeEntity> collect = typeEntityList.stream().filter(t -> dictionaryTypeEntity.getId().equals(t.getParentId())).collect(Collectors.toList());
        if (collect.size() > 0) {
            for (DictionaryTypeEntity typeEntity : collect) {
                set.add(typeEntity);
                getDictionaryTypeEntitySet(typeEntity, set, typeEntityList);
            }
        }
    }

    @Override
    @DSTransactional
    public boolean importData(DictionaryExportModel exportModel) throws DataException {
        try {
            boolean isExists = true;
            List<DictionaryTypeEntity> list = JsonUtil.getJsonToList(exportModel.getList(), DictionaryTypeEntity.class);
            List<DictionaryDataEntity> entityList = JsonUtil.getJsonToList(exportModel.getModelList(), DictionaryDataEntity.class);
            //遍历插入分类
            for (DictionaryTypeEntity entity : list) {
                if (dictionaryTypeService.getInfo(entity.getId()) == null
                        && !dictionaryTypeService.isExistByEnCode(entity.getEnCode(), entity.getId())
                        && !dictionaryTypeService.isExistByFullName(entity.getFullName(), entity.getId())
                ) {
                    isExists = false;
                    dictionaryTypeService.create(entity);
                }
            }
            for (DictionaryDataEntity entity1 : entityList) {
                DictionaryDataEntity dataEntity = getInfo(entity1.getId());
                if (dataEntity == null && dictionaryTypeService.getInfo(entity1.getDictionaryTypeId()) != null
                        && !isExistByFullName(entity1.getDictionaryTypeId(), entity1.getFullName(), entity1.getId())
                        && !isExistByEnCode(entity1.getDictionaryTypeId(), entity1.getEnCode(), entity1.getId())
                ) {
                    isExists = false;
                    create(entity1);
                }
            }
            return isExists;
        } catch (Exception e) {
            //手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new DataException(e.getMessage());
        }
    }

}
