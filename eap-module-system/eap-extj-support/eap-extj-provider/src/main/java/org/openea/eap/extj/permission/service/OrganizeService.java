package org.openea.eap.extj.permission.service;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.model.organize.OrganizeConditionModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 组织机构
 *
 *
 */
public interface OrganizeService extends SuperService<OrganizeEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<OrganizeEntity> getListAll(List<String> idAll, String keyWord);

    /**
     * 查询所有
     */
    List<OrganizeEntity> getAllCompanyList(String keyWord);

    /**
     * 列表
     *
     * @return
     */
    List<OrganizeEntity> getParentIdList(String id);

    /**
     * 列表
     *
     * @return
     */
    List<OrganizeEntity> getList();

    /**
     * 列表(有效的组织)
     *
     * @return
     */
    List<OrganizeEntity> getListById(Boolean enable);

    /**
     * 列表
     *
     * @param fullName 组织名称
     * @return
     */
    OrganizeEntity getIdListByFullName(String fullName);

    /**
     * 列表
     *
     * @return
     */
    List<OrganizeEntity> getList(String keyword);

    /**
     * 获取部门名列表
     *
     * @return
     */
    List<OrganizeEntity> getOrgEntityList(List<String> idList, Boolean enable);

    /**
     * 获取部门名列表(在线开发转换数据使用)
     *
     * @return
     */
    List<OrganizeEntity> getOrgEntityList(Set<String> idList);

    /**
     * 全部组织（id : name）
     * @return
     */
    Map<String, Object> getOrgMap();

    /**
     * 全部组织（Encode/name : id）
     * @return
     * @param type
     */
    Map<String, Object> getOrgEncodeAndName(String type);

    /**
     * 全部组织（name : id）
     * @return
     * @param type
     */
    Map<String, Object> getOrgNameAndId(String type);

    /**
     * 获取redis存储的部门信息
     *
     * @return
     */
    List<OrganizeEntity> getOrgRedisList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    OrganizeEntity getInfo(String id);

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @return
     */
    OrganizeEntity getByFullName(String fullName);

    /**
     * 通过名称 组织类型 查询id
     *
     * @param fullName 名称
     * @param category 类别
     * @param enCode 编码
     * @return
     */
    OrganizeEntity getByFullName(String fullName,String category,String enCode);

    /**
     * 验证名称
     *
     * @param entity
     * @param isCheck  组织名称是否不分级判断
     * @param isFilter 是否需要过滤id
     * @return
     */
    boolean isExistByFullName(OrganizeEntity entity, boolean isCheck, boolean isFilter);

    /**
     * 获取父级id
     *
     * @param organizeId           组织id
     * @param organizeParentIdList 父级id集合
     */
    void getOrganizeIdTree(String organizeId, List<String> organizeParentIdList);

    /**
     * 获取父级id
     *
     * @param organizeId           组织id
     * @param organizeParentIdList 父级id集合
     */
    void getOrganizeId(String organizeId, List<OrganizeEntity> organizeParentIdList);

    /**
     * 验证编码
     *
     * @param enCode
     * @param id
     * @return
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(OrganizeEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    boolean update(String id, OrganizeEntity entity);

    /**
     * 通过父级id修改父级组织树
     *
     * @param entity
     * @param category
     */
    void update(OrganizeEntity entity, String category);

    /**
     * 删除
     *
     * @param orgId 实体对象
     */
    ActionResult<String> delete(String orgId);

    /**
     * 上移
     *
     * @param id 主键值
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     */
    boolean next(String id);

    /**
     * 判断是否允许删除
     *
     * @param orgId 主键值
     * @return
     */
    String allowDelete(String orgId);

    /**
     * 获取名称
     *
     * @return
     */
    List<OrganizeEntity> getOrganizeName(List<String> id);

    /**
     * 获取名称
     *
     * @return
     */
    List<OrganizeEntity> getOrganizeName(List<String> id, String keyword);

    /**
     * 获取名称
     *
     * @return
     */
    List<OrganizeEntity> getOrganizeNameSort(List<String> id);

    /**
     * @param organizeParentId 父id
     * @return List<String> 接收子结构
     */
    List<String> getOrganize(String organizeParentId);

    /**
     * @param organizeParentId 父id
     * @return List<String> 接收子结构
     */
    List<String> getOrganizeByOraParentId(String organizeParentId);

    /**
     * 获取所有当前用户的组织及子组织
     *
     * @param organizeId
     * @return
     */
    List<String> getUnderOrganizations(String organizeId);

    /**
     * 获取所有当前用户的组织及子组织 (有分级权限验证)
     *
     * @param organizeId
     * @return
     */
    List<String> getUnderOrganizationss(String organizeId);

    /**
     * 通过名称获取组织列表
     *
     * @param fullName
     * @return
     */
    List<OrganizeEntity> getListByFullName(String fullName);

    /**
     * 通过id判断是否有子集
     *
     * @param id 主键
     * @return
     */
    List<OrganizeEntity> getListByParentId(String id);

    /**
     * 获取用户所有所在组织
     *
     * @return 组织对象集合
     */
    List<OrganizeEntity> getAllOrgByUserId(String userId);

    /**
     * 通过组织id树获取名称
     *
     * @param orgIdTree 组织id树
     * @param regex     分隔符
     * @return 组织对象集合
     */
    String getFullNameByOrgIdTree(String orgIdTree, String regex);

    /**
     * 获取父级组织id
     *
     * @param entity
     * @return
     */
    String getOrganizeIdTree(OrganizeEntity entity);

    /**
     * 获取顶级组织
     *
     * @return
     */
    List<OrganizeEntity> getOrganizeByParentId();

    /**
     * 查询用户的所属公司下的部门
     *
     * @return
     */
    List<OrganizeEntity> getDepartmentAll(String organizeId);

    /**
     * 获取所在公司
     *
     * @param organizeId
     * @return
     */
    OrganizeEntity getOrganizeCompany(String organizeId);

    /**
     * 获取所在公司下部门
     *
     * @return
     */
    void getOrganizeDepartmentAll(String organize, List<OrganizeEntity> list);

    /**
     * 获取组织id树
     *
     * @param entity
     * @return
     */
    List<String> getOrgIdTree(OrganizeEntity entity);

    /**
     * 向上递归取组织id
     * @param orgID
     * @return
     */
    List<String> upWardRecursion(List<String> orgIDs, String orgID);

    /**
     * 查询给定的条件是否有默认当前登录者的默认部门值
     * @param organizeConditionModel
     * @return
     */
    String getDefaultCurrentValueDepartmentId(OrganizeConditionModel organizeConditionModel);

    /**
     * 获取名称及id组成map
     *
     * @return
     */
    Map<String, OrganizeEntity> getInfoList();
}
