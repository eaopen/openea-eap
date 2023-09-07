package org.openea.eap.extj.portal.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.model.VisualFunctionModel;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.DictionaryTypeService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.base.service.SystemService;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.portal.constant.PortalConst;
import org.openea.eap.extj.portal.entity.PortalEntity;
import org.openea.eap.extj.portal.entity.PortalManageEntity;
import org.openea.eap.extj.portal.mapper.PortalMapper;
import org.openea.eap.extj.portal.model.PortalPagination;
import org.openea.eap.extj.portal.model.PortalSelectModel;
import org.openea.eap.extj.portal.model.PortalSelectVO;
import org.openea.eap.extj.portal.model.PortalViewPrimary;
import org.openea.eap.extj.portal.model.portalManage.PortalManagePage;
import org.openea.eap.extj.portal.model.portalManage.PortalManagePageDO;
import org.openea.eap.extj.portal.model.portalManage.PortalManagePrimary;
import org.openea.eap.extj.portal.model.portalManage.PortalManageVO;
import org.openea.eap.extj.portal.service.PortalDataService;
import org.openea.eap.extj.portal.service.PortalManageService;
import org.openea.eap.extj.portal.service.PortalService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.enums.DictionaryDataEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**

 */
@Service
public class PortalServiceImpl extends SuperServiceImpl<PortalMapper, PortalEntity> implements PortalService {

    @Autowired
    private PortalService portalService;
    @Autowired
    private PortalManageService portalManageService;
    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private PortalDataService portalDataService;
    @Autowired
    private SystemService systemApi;

    @Override
    public List<PortalEntity> getList(PortalPagination portalPagination) {
        return getList(portalPagination, new QueryWrapper<>());
    }

    public List<PortalEntity> getList(PortalPagination portalPagination, QueryWrapper<PortalEntity> queryWrapper) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        // 模糊查询
        if (!StringUtil.isEmpty(portalPagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(q-> q.like(PortalEntity::getFullName, portalPagination.getKeyword()).or()
                    .like(PortalEntity::getEnCode, portalPagination.getKeyword()));
        }
        // 分类（数据字典）
        if (StringUtil.isNotEmpty(portalPagination.getCategory())) {
            flag = true;
            queryWrapper.lambda().eq(PortalEntity::getCategory, portalPagination.getCategory());
        }
        // 类型(0-页面设计,1-自定义路径)
        if (portalPagination.getType() != null) {
            flag = true;
            queryWrapper.lambda().eq(PortalEntity::getType, portalPagination.getType());
        }
        // 锁定
        if (portalPagination.getEnabledLock() != null) {
            flag = true;
            queryWrapper.lambda().eq(PortalEntity::getEnabledLock, portalPagination.getEnabledLock());
        }
        // 排序
        queryWrapper.lambda().orderByAsc(PortalEntity::getSortCode).orderByDesc(PortalEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(PortalEntity::getLastModifyTime);
        }
        // 分页
        Page<PortalEntity> page = new Page<>(portalPagination.getCurrentPage(), portalPagination.getPageSize());
        IPage<PortalEntity> userPage = this.page(page, queryWrapper);
        return portalPagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public PortalEntity getInfo(String id) {
        QueryWrapper<PortalEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PortalEntity::getId, id);
        return this.getOne(queryWrapper);
    }


    @Override
    public Boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<PortalEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PortalEntity::getFullName, fullName);
        return isExistCommon(queryWrapper, id);
    }

    @Override
    public Boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<PortalEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PortalEntity::getEnCode, enCode);
        return isExistCommon(queryWrapper, id);
    }

    private Boolean isExistCommon(QueryWrapper<PortalEntity> queryWrapper, String id){
        if (!StringUtil.isEmpty(id)) queryWrapper.lambda().ne(PortalEntity::getId, id);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public void create(PortalEntity entity) {
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(RandomUtil.uuId());
        }
        this.saveOrUpdateIgnoreLogic(entity);
    }

    @Override
    public Boolean update(String id, PortalEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(PortalEntity entity) throws Exception{
        // 0、门户管理判断（已绑定系统不允许删除）
        List<PortalManageVO> portalManageList = portalManageService.getList(new PortalManagePrimary(null, entity.getId(), null));
        if (portalManageList.size() > 0) {
            List<String> sysNameList = portalManageList.stream().map(manage -> {
                try{
                    return systemApi.getInfo(manage.getSystemId()).getFullName();
                }catch (Exception ignore){ return ""; }
            }).collect(Collectors.toList());
            String sysName = sysNameList.get(0);
            StringBuffer buffer = new StringBuffer();
            buffer.append("此记录被\"【"+sysName+"】应用门户\"关联引用,不允许被删除");
            throw new Exception(buffer.toString());
        }
        // 1、删除门户设置数据（数据删除可提示用户确认）
        portalDataService.deleteAll(entity.getId());
        portalService.removeById(entity.getId());
    }

    @Override
    public List<PortalSelectVO> getManageSelectorPage(PortalPagination pagination, String systemId) {
        // 根据系统ID、平台获取
        List<PortalManageVO> manageVOList = portalManageService.getList(new PortalManagePrimary(pagination.getPlatform(), null, systemId));
        Set<String> usedPortalIds = manageVOList.stream().map(PortalManageVO::getPortalId).collect(Collectors.toSet());
        QueryWrapper<PortalEntity> query = new QueryWrapper<>();
        // 已绑定的门户，不再出现在下拉列表
        if(usedPortalIds.size() > 0) query.lambda().notIn(PortalEntity::getId, usedPortalIds);
        query.lambda().eq(PortalEntity::getEnabledMark, 1);
        List<PortalEntity> portalList = getList(pagination, query);
        List<PortalSelectVO> voList = new ArrayList<>();
        portalList.forEach(entity->{
            PortalSelectVO vo = new PortalSelectVO();
            vo.setId(entity.getId());
            vo.setFullName(entity.getFullName());
            vo.setEnCode(entity.getEnCode());
            DictionaryDataEntity dicEntity = dictionaryDataService.getInfo(entity.getCategory());
            vo.setCategory(dicEntity.getFullName());
            vo.setCategoryName(dicEntity.getFullName());
            vo.setCategoryId(dicEntity.getId());
            voList.add(vo);
        });
        return voList;
    }

    @Override
    public String getModListFirstId(PortalViewPrimary primary){
        try{
            List<PortalSelectModel> modList = getModList(primary);
            PortalSelectModel first = modList.stream().filter(mod -> mod.getParentId().equals("0")).findFirst().get();
            PortalSelectModel firstPortal = modList.stream().filter(mod -> mod.getParentId().equals(first.getId())).findFirst().get();
            return firstPortal.getId();
        }catch (Exception e){
            return "";
        }
    }

    @Override
    public List<PortalSelectModel> getModList(PortalViewPrimary primary) {
        UserInfo userInfo = userProvider.get();
        List<String> portalIds;
        if(userInfo.getIsAdministrator()){
            portalIds = portalManageService.getListByEnable(new PortalManagePrimary(primary.getPlatForm(), null, primary.getSystemId()))
                .stream().map(PortalManageVO::getPortalId).collect(Collectors.toList());
        }else {
            portalIds = portalDataService.getCurrentAuthPortalIds(primary);
        }
        PortalManagePage page =new PortalManagePage();
        page.setEnabledMark(1);
        page.setPlatform(primary.getPlatForm());
        page.setSystemId(primary.getSystemId());
        List<PortalManagePageDO> selectList = portalManageService.getSelectList(page);
        List<PortalEntity> resultList = new ArrayList<>();
        for (PortalManagePageDO portalManagePageDO : selectList) {
            if(portalIds.contains(portalManagePageDO.getPortalId())){
                PortalEntity entity = new PortalEntity();
                entity.setId(portalManagePageDO.getPortalId());
                entity.setFullName(portalManagePageDO.getPortalName());
                entity.setCategory(portalManagePageDO.getCategoryId());
                resultList.add(entity);
            }
        }
        if(CollectionUtil.isNotEmpty(portalIds)){
            return getModelList(resultList);
        }else {
            return new ArrayList<>();
        }
    }

    private List<PortalSelectModel> getModelList(List<PortalEntity> portalList){
        List<PortalSelectModel> modelList = JsonUtil.getJsonToList(portalList, PortalSelectModel.class);
        // 外层菜单排序取数据字典
        List<DictionaryDataEntity> dictionaryList = dictionaryDataService
                .getList(dictionaryTypeService.getInfoByEnCode(DictionaryDataEnum.VISUALDEV_PORTAL.getDictionaryTypeId()).getId());
        for (DictionaryDataEntity dictionary : dictionaryList) {
            List<PortalSelectModel> models = modelList.stream().filter(model->model.getParentId().equals(dictionary.getId())).collect(Collectors.toList());
            if(models.size() > 0){
                PortalSelectModel model = new PortalSelectModel();
                model.setId(dictionary.getId());
                model.setFullName(dictionary.getFullName());
                model.setParentId("0");
                if (!modelList.contains(model)) {
                    modelList.add(model);
                }
            }
        }
        return modelList;
    }

    @Override
    public List<VisualFunctionModel> getModelList(PortalPagination pagination) {
        List<PortalEntity> data = portalService.getList(pagination);
        List<String> userId = data.stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList());
        List<String> lastUserId = data.stream().map(t -> t.getLastModifyUserId()).collect(Collectors.toList());
        List<UserEntity> userEntities = userService.getUserName(userId);
        List<UserEntity> lastUserIdEntities = userService.getUserName(lastUserId);
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getList(dictionaryTypeService.getInfoByEnCode(DictionaryDataEnum.VISUALDEV_PORTAL.getDictionaryTypeId()).getId());
        List<VisualFunctionModel> modelAll = new LinkedList<>();
        // 发布判断
        List<PortalManageEntity> isReleaseList = portalManageService.list();

        for (PortalEntity entity : data) {
            VisualFunctionModel model = JsonUtil.getJsonToBean(entity, VisualFunctionModel.class);
            model.setPcIsRelease(isReleaseList.stream().anyMatch(vo-> vo.getPortalId().equalsIgnoreCase(entity.getId())
                    && PortalConst.WEB.equalsIgnoreCase(vo.getPlatform())) ? 1 : 0);
            model.setAppIsRelease(isReleaseList.stream().anyMatch(vo-> vo.getPortalId().equalsIgnoreCase(entity.getId())
                    && PortalConst.APP.equalsIgnoreCase(vo.getPlatform())) ? 1 : 0);
            DictionaryDataEntity dataEntity = dictionList.stream().filter(t -> t.getId().equals(entity.getCategory())).findFirst().orElse(null);
            if (dataEntity != null) {
                model.setCategory(dataEntity.getFullName());
                UserEntity creatorUser = userEntities.stream().filter(t -> t.getId().equals(model.getCreatorUserId())).findFirst().orElse(null);
                if (creatorUser != null) {
                    model.setCreatorUser(creatorUser.getRealName() + "/" + creatorUser.getAccount());
                } else {
                    model.setCreatorUser("");
                }
                UserEntity lastmodifyuser = lastUserIdEntities.stream().filter(t -> t.getId().equals(model.getLastModifyUserId())).findFirst().orElse(null);
                if (lastmodifyuser != null) {
                    model.setLastModifyUser(lastmodifyuser.getRealName() + "/" + lastmodifyuser.getAccount());
                } else {
                    model.setLastModifyUser("");
                }
                if (Objects.isNull(model.getSortCode())) {
                    model.setSortCode(0L);
                }
                modelAll.add(model);
            }
        }
        return modelAll.stream().sorted(Comparator.comparing(VisualFunctionModel::getSortCode)).collect(Collectors.toList());
    }

}
