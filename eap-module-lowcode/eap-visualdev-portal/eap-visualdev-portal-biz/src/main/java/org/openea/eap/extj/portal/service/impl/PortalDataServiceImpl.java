package org.openea.eap.extj.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.openea.eap.extj.base.MyBatisPrimaryBase;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.base.service.SystemService;
import org.openea.eap.extj.permission.entity.AuthorizeEntity;
import org.openea.eap.extj.permission.entity.RoleEntity;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.AuthorizeService;
import org.openea.eap.extj.permission.service.RoleService;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.portal.constant.PortalConst;
import org.openea.eap.extj.portal.entity.PortalDataEntity;
import org.openea.eap.extj.portal.entity.PortalEntity;
import org.openea.eap.extj.portal.entity.PortalManageEntity;
import org.openea.eap.extj.portal.mapper.PortalDataMapper;
import org.openea.eap.extj.portal.model.PortalInfoAuthVO;
import org.openea.eap.extj.portal.model.PortalModPrimary;
import org.openea.eap.extj.portal.model.PortalReleasePrimary;
import org.openea.eap.extj.portal.model.PortalViewPrimary;
import org.openea.eap.extj.portal.service.PortalDataService;
import org.openea.eap.extj.portal.service.PortalManageService;
import org.openea.eap.extj.portal.service.PortalService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.portal.model.*;
import org.openea.eap.extj.portal.model.portalManage.PortalManagePrimary;
import org.openea.eap.extj.portal.model.portalManage.PortalManageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class PortalDataServiceImpl extends SuperServiceImpl<PortalDataMapper, PortalDataEntity> implements PortalDataService {

    @Autowired
    private SystemService systemService;
    @Autowired
    private UserService userService;
    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private PortalService portalService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private PortalManageService portalManageApi;

    @Override
    public String getCustomDataForm(PortalCustomPrimary primary) throws Exception {
        PortalDataEntity one = getOne(primary.getQuery());
        if(one != null){
            return one.getFormData();
        }else {
            save(primary.getEntity());
        }
        return "";
    }

    @Override
    public String getModelDataForm(PortalModPrimary primary) throws Exception {
        PortalDataEntity one = getOne(primary.getQuery());
        if(one != null){
            return one.getFormData();
        }else {
            save(primary.getEntity());
        }
        return "";
    }

    @Override
    public void release(String platform, String portalId, String systemIdListStr,String releasePlatform) throws Exception {
        List<String> systemIdList;
        if(StringUtils.isNotEmpty(systemIdListStr)){
            systemIdList = Arrays.asList(systemIdListStr.split(","));
            // 系统管理对应添加绑定
            portalManageApi.createBatch(systemIdList.stream().map(systemId->
                    new PortalManagePrimary(releasePlatform, portalId, systemId)).collect(Collectors.toList()));
        }else {
            List<PortalManageVO> voList = portalManageApi.getList(new PortalManagePrimary(platform, portalId, null));
            systemIdList = voList.stream().map(PortalManageVO::getSystemId).collect(Collectors.toList());
        }
        String formData = "";
        try {
            formData = getModelDataForm(new PortalModPrimary(portalId));
            createOrUpdate(new PortalReleasePrimary(portalId, platform), formData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 查询所有相关的自定义数据
        for (String systemId : systemIdList) {
            List<PortalDataEntity> list = list(new PortalCustomPrimary(platform, portalId, systemId, null).getQuery());
            final String finalFormData = formData;
            if(list.size() > 0){
                // 把所有数据进行重置formData
                list.forEach(entity -> entity.setFormData(finalFormData));
                updateBatchById(list);
            }
        }
    }

    @Override
    public Boolean isReleaseFlag(PortalReleasePrimary primary) {
        return count(primary.getQuery()) > 0;
    }

    @Override
    public Boolean deleteAll(String portalId) {
        QueryWrapper<PortalDataEntity> query = new QueryWrapper<>();
        query.lambda().eq(PortalDataEntity::getPortalId, portalId);
        return remove(query);
    }

    /**
     * 创建或更新
     *
     *  门户ID ->（平台、系统ID、用户ID）-> 排版信息
     *  基础：门户ID绑定排版信息（一对多）、 条件：平台、系统ID、用户ID
     */
    @Override
    public void createOrUpdate(PortalCustomPrimary primary, String formData) throws Exception {
        creUpCom(primary, formData);
    }

    @Override
    public void createOrUpdate(PortalModPrimary primary, String formData) throws Exception {
        creUpCom(primary, formData);
    }

    @Override
    public void createOrUpdate(PortalReleasePrimary primary, String formData) throws Exception {
        creUpCom(primary, formData);
    }

    private void creUpCom(MyBatisPrimaryBase<PortalDataEntity> primary, String formData) throws Exception {
        // 自定义数据变量条件：0、门户 1、用户 2、系统 3、平台
        List<PortalDataEntity> list = list(primary.getQuery());
        if(list.size() < 1){
            PortalDataEntity creEntity = primary.getEntity();
            creEntity.setFormData(formData);
            String authorSignature = ServletUtil.getRequest().getHeader(Constants.AUTHORIZATION);
            UserInfo userInfo = userProvider.get(authorSignature.substring(7,authorSignature.length()));

            creEntity.setCreatorUserId(userInfo.getUserId());
            if (StringUtil.isEmpty(creEntity.getId())) {
                creEntity.setId(RandomUtil.uuId());
            }
            save(creEntity);
        }else if(list.size() == 1){
            PortalDataEntity upEntity = list.get(0);
            upEntity.setFormData(formData);
            updateById(upEntity);
        }else {
            throw new Exception("门户数据信息存在重复");
        }
    }

    /**
     * 根据id返回门户信息
     */
    @Override
    public PortalInfoAuthVO getDataFormView(String portalId, String platform) throws Exception{
        PortalEntity entity = portalService.getInfo(portalId);
        if (entity == null) throw new Exception("门户未找到");
//        if (entity.getEnabledMark() == 0) throw new Exception("门户被禁止");
        PortalInfoAuthVO infoVo = JsonUtil.getJsonToBean(JsonUtilEx.getObjectToStringDateFormat
                (entity, "yyyy-MM-dd HH:mm:ss"), PortalInfoAuthVO.class);
        // 查询自定义设计的门户信息
        infoVo.setFormData(getDataForm(platform, portalId));
        return infoVo;
    }

    private String getDataForm(String platform, String portalId) throws Exception {
        List<PortalDataEntity> dataList = list(new PortalCustomPrimary(platform, portalId).getQuery());
        if(CollectionUtil.isEmpty(dataList)) dataList = list(new PortalReleasePrimary(portalId, platform).getQuery());
        // 当没有自定义的排版信息时，使用已发布模板排版信息
        if(CollectionUtil.isNotEmpty(dataList)){
            PortalDataEntity entity = dataList.get(0);
            if (dataList.size() != 1) {
                List<String> ids = dataList.stream().map(PortalDataEntity::getId).collect(Collectors.toList());
                removeBatchByIds(ids.stream().filter(id -> !id.equals(entity.getId())).collect(Collectors.toList()));
            }
            return entity.getFormData();
        }
        return null;
    }

    /**
     * 设置门户默认主页
     *
     * 用户ID -> (平台、系统ID) -> 门户ID
     * 基础：用户ID绑定门户ID（多对多）、条件：平台、系统ID
     * Map格式：Map <platform:systemId, portalId>
     * @param portalId 门户ID
     * @param platform 平台
     */
    @Override
    public void setCurrentDefault(String platform, String portalId) {
        UserEntity userEntity = userService.getInfo(userProvider.get().getUserId());
        Map<String, Object> map = new HashMap<>();
        try{
            map = JSONObject.parseObject(userEntity.getPortalId()).getInnerMap();
        }catch (Exception ignore){}
        map.put(platform + ":" + userEntity.getSystemId(), portalId);
        UserEntity update = new UserEntity();
        update.setId(userEntity.getId());
        update.setPortalId(JSONObject.toJSONString(map));
        userService.updateById(update);
    }

    @Override
    public String getCurrentDefault(String platform) throws Exception {
        UserEntity userEntity = userService.getInfo(userProvider.get().getUserId());
        String systemId = userProvider.get().getSystemId();
        String portalId = "";
        try{
            Map<String, Object> map = JSONObject.parseObject(userEntity.getPortalId()).getInnerMap();
            portalId = map.get(platform + ":" + systemId).toString();
        }catch (Exception ignore){}
        PortalEntity mainPortal = portalService.getById(portalId);
        // 校验门户有效性
        if(mainPortal != null && mainPortal.getEnabledMark().equals(1)){
            // 管理员直接设置默认主页
            List<String> authPortalIds;
            if(userProvider.get().getIsAdministrator()){
                List<PortalManageVO> currentVoList = portalManageApi.getListByEnable(new PortalManagePrimary(platform, null, systemId));
                authPortalIds = currentVoList.stream().map(PortalManageVO::getPortalId).collect(Collectors.toList());
            }else {
                // 获取当前用户的所有权限的门户ID集合
                authPortalIds = getCurrentAuthPortalIds(new PortalViewPrimary(platform, null));
            }
            if(CollectionUtil.isNotEmpty(authPortalIds) && authPortalIds.contains(portalId)) {
                return portalId;
            }
        }
        // 重新设置默认门户
        String updatePortalId = portalService.getModListFirstId(new PortalViewPrimary(platform, null));
        setCurrentDefault(platform, updatePortalId);
        return updatePortalId;
    }

    /**
     * 获取当下所有带权限PortalId集合
     */
    public List<String> getCurrentAuthPortalIds(PortalViewPrimary primary){
        String userId = userProvider.get().getUserId();
        String systemId = userProvider.get().getSystemId();


        // 获取用户底下所有权限portalManage
        Supplier<List<String>> authPortalManageIds = ()->{
            //List<String> roleIds = roleService.getListByUserId(userId).stream().map(RoleEntity::getId).collect(Collectors.toList());
            List<String> roleIds = new ArrayList<>();
            Collection<RoleEntity> roles = roleService.getListByUserId(userId);
            if(roles!=null){
                for(RoleEntity role:roles){
                    roleIds.add(role.getId());
                }
            }
            List<String> portalManageIds = new ArrayList<>();
            for (String roleId : roleIds) {
            /* authorize存储 portalManage->item、role->object，本质：门户管理条目与角色关系
            根据用户Id查询出对应所有的门户管理条目(distinct()进行去重) */
                List<AuthorizeEntity> authorizePortalManage = authorizeService.getListByObjectId(roleId, PortalConst.AUTHORIZE_PORTAL_MANAGE);
                portalManageIds.addAll(authorizePortalManage.stream()
                        .map(AuthorizeEntity::getItemId).distinct().collect(Collectors.toList()));
            }
            return portalManageIds;
        };

        List<String> portalManageIds = authPortalManageIds.get();

        Map<String, List<String>> map = new HashMap<>();
        // 获取具有权限的所有门户
        if(portalManageIds.size() > 0) {
            QueryWrapper<PortalManageEntity> query = new QueryWrapper<>();
            query.lambda().eq(PortalManageEntity::getEnabledMark, 1)
                          .eq(PortalManageEntity::getPlatform, primary.getPlatForm())
                          .in(PortalManageEntity::getId, portalManageIds);
            List<PortalManageEntity> portalManageList = portalManageApi.list(query);
            Map<String, List<PortalManageEntity>> collect = portalManageList.stream().collect(Collectors.groupingBy(PortalManageEntity::getSystemId));
            // key:systemId 、 value:portalIdList
            collect.forEach((key, description) -> map.put(key, description.stream().map(PortalManageEntity::getPortalId).collect(Collectors.toList())));
            return map.get(systemId);
        }else {
            return new ArrayList<>();
        }
    }


    /* ============== 目前使用懒加载更新主页，故注释 =============== */
//    /**
//     *  校验：当前用户的默认门户首页是否还具备权限
//     *  （当权限被删除，默认首页丢失）
//     */
//    private void checkDefaultPortal(List<String> originRoleIds, List<String> updateRoleIds, String portalManageId){
//        // 若是新增角色不会对默认门户产生影响
//        List<String> delRoleIds = originRoleIds.stream().filter(o -> !updateRoleIds.contains(o))
//                .collect(Collectors.toList());
//        if(CollectionUtil.isNotEmpty(delRoleIds)){
//            // 授权取消所涉及到用户集合
//            List<UserEntity> userList = userService.getListByRoleIds(delRoleIds);
//            for (UserEntity userEntity : userList) {
//                String userId = userEntity.getId();
//                // 获取各系统下带权限的门户ID（这里跨服务会产生数据不同步的问题）
//                Map<String, List<String>> authorizeSysPortalIdsMap = getAuthorizePortalIds(userId);
//                // 获取原始各系统下默认门户ID
//                Map<String, String> sysDefaultPortalIdMap = portalManageService.getDefault(userEntity.getId());
//                for (Map.Entry<String, String> defaultMap : sysDefaultPortalIdMap.entrySet()) {
//                    String systemId = defaultMap.getKey();
//                    // 默认门户ID
//                    String defaultPortalId = defaultMap.getValue();
//                    // 带权限门户ID
//                    List<String> authorizePortalIds = authorizeSysPortalIdsMap.get(systemId);
//                    if(CollectionUtil.isNotEmpty(authorizePortalIds)){
//                        if(!authorizePortalIds.contains(defaultPortalId)){
//                            // 丢失默认门户，顺位首个
//                            portalManageService.setDefault(userId, systemId, authorizePortalIds.get(0));
//                        }
//                    }
//                }
//            }
//        }
//    }



}
