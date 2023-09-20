package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.model.organize.OrganizeConditionModel;
import org.openea.eap.extj.permission.service.OrganizeService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class EapOrganizeService implements OrganizeService {
    @Override
    public List<String> getUnderOrganizations(String orgId) {
        return null;
    }

    /**
     * 获取所有当前用户的组织及子组织 (有分级权限验证)
     *
     * @param organizeId
     * @return
     */
    @Override
    public List<String> getUnderOrganizationss(String organizeId) {
        return null;
    }

    /**
     * 通过名称获取组织列表
     *
     * @param fullName
     * @return
     */
    @Override
    public List<OrganizeEntity> getListByFullName(String fullName) {
        return null;
    }

    /**
     * 通过id判断是否有子集
     *
     * @param id 主键
     * @return
     */
    @Override
    public List<OrganizeEntity> getListByParentId(String id) {
        return null;
    }

    /**
     * 获取用户所有所在组织
     *
     * @param userId
     * @return 组织对象集合
     */
    @Override
    public List<OrganizeEntity> getAllOrgByUserId(String userId) {
        return null;
    }

    /**
     * 通过组织id树获取名称
     *
     * @param orgIdTree 组织id树
     * @param regex     分隔符
     * @return 组织对象集合
     */
    @Override
    public String getFullNameByOrgIdTree(String orgIdTree, String regex) {
        return null;
    }

    /**
     * 获取父级组织id
     *
     * @param entity
     * @return
     */
    @Override
    public String getOrganizeIdTree(OrganizeEntity entity) {
        return null;
    }

    /**
     * 获取顶级组织
     *
     * @return
     */
    @Override
    public List<OrganizeEntity> getOrganizeByParentId() {
        return null;
    }

    /**
     * 查询用户的所属公司下的部门
     *
     * @param organizeId
     * @return
     */
    @Override
    public List<OrganizeEntity> getDepartmentAll(String organizeId) {
        return null;
    }

    /**
     * 获取所在公司
     *
     * @param organizeId
     * @return
     */
    @Override
    public OrganizeEntity getOrganizeCompany(String organizeId) {
        return null;
    }

    /**
     * 获取所在公司下部门
     *
     * @param organize
     * @param list
     * @return
     */
    @Override
    public void getOrganizeDepartmentAll(String organize, List<OrganizeEntity> list) {

    }

    /**
     * 获取组织id树
     *
     * @param entity
     * @return
     */
    @Override
    public List<String> getOrgIdTree(OrganizeEntity entity) {
        return null;
    }

    /**
     * 向上递归取组织id
     *
     * @param orgIDs
     * @param orgID
     * @return
     */
    @Override
    public List<String> upWardRecursion(List<String> orgIDs, String orgID) {
        return null;
    }

    /**
     * 查询给定的条件是否有默认当前登录者的默认部门值
     *
     * @param organizeConditionModel
     * @return
     */
    @Override
    public String getDefaultCurrentValueDepartmentId(OrganizeConditionModel organizeConditionModel) {
        return null;
    }

    /**
     * 获取名称及id组成map
     *
     * @return
     */
    @Override
    public Map<String, OrganizeEntity> getInfoList() {
        return null;
    }

    @Override
    public OrganizeEntity getInfo(String organizeId) {
        return null;
    }

    /**
     * 通过名称查询id
     *
     * @param fullName 名称
     * @return
     */
    @Override
    public OrganizeEntity getByFullName(String fullName) {
        return null;
    }

    /**
     * 通过名称 组织类型 查询id
     *
     * @param fullName 名称
     * @param category 类别
     * @param enCode   编码
     * @return
     */
    @Override
    public OrganizeEntity getByFullName(String fullName, String category, String enCode) {
        return null;
    }

    /**
     * 验证名称
     *
     * @param entity
     * @param isCheck  组织名称是否不分级判断
     * @param isFilter 是否需要过滤id
     * @return
     */
    @Override
    public boolean isExistByFullName(OrganizeEntity entity, boolean isCheck, boolean isFilter) {
        return false;
    }

    /**
     * 获取父级id
     *
     * @param organizeId           组织id
     * @param organizeParentIdList 父级id集合
     */
    @Override
    public void getOrganizeIdTree(String organizeId, List<String> organizeParentIdList) {

    }

    /**
     * 获取父级id
     *
     * @param organizeId           组织id
     * @param organizeParentIdList 父级id集合
     */
    @Override
    public void getOrganizeId(String organizeId, List<OrganizeEntity> organizeParentIdList) {

    }

    /**
     * 验证编码
     *
     * @param enCode
     * @param id
     * @return
     */
    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        return false;
    }

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    @Override
    public void create(OrganizeEntity entity) {

    }

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    @Override
    public boolean update(String id, OrganizeEntity entity) {
        return false;
    }

    /**
     * 通过父级id修改父级组织树
     *
     * @param entity
     * @param category
     */
    @Override
    public void update(OrganizeEntity entity, String category) {

    }

    /**
     * 删除
     *
     * @param orgId 实体对象
     */
    @Override
    public ActionResult<String> delete(String orgId) {
        return null;
    }

    /**
     * 上移
     *
     * @param id 主键值
     */
    @Override
    public boolean first(String id) {
        return false;
    }

    /**
     * 下移
     *
     * @param id 主键值
     */
    @Override
    public boolean next(String id) {
        return false;
    }

    /**
     * 判断是否允许删除
     *
     * @param orgId 主键值
     * @return
     */
    @Override
    public String allowDelete(String orgId) {
        return null;
    }

    /**
     * 获取名称
     *
     * @param id
     * @return
     */
    @Override
    public List<OrganizeEntity> getOrganizeName(List<String> id) {
        return null;
    }

    /**
     * 获取名称
     *
     * @param id
     * @param keyword
     * @return
     */
    @Override
    public List<OrganizeEntity> getOrganizeName(List<String> id, String keyword) {
        return null;
    }

    /**
     * 获取名称
     *
     * @param id
     * @return
     */
    @Override
    public List<OrganizeEntity> getOrganizeNameSort(List<String> id) {
        return null;
    }

    /**
     * @param organizeParentId 父id
     * @return List<String> 接收子结构
     */
    @Override
    public List<String> getOrganize(String organizeParentId) {
        return null;
    }

    /**
     * @param organizeParentId 父id
     * @return List<String> 接收子结构
     */
    @Override
    public List<String> getOrganizeByOraParentId(String organizeParentId) {
        return null;
    }

    @Override
    public List<OrganizeEntity> getOrgRedisList() {
        return null;
    }

    @Override
    public Map<String, Object> getOrgMap() {
        return null;
    }

    /**
     * 列表
     *
     * @param idAll
     * @param keyWord
     * @return
     */
    @Override
    public List<OrganizeEntity> getListAll(List<String> idAll, String keyWord) {
        return null;
    }

    /**
     * 查询所有
     *
     * @param keyWord
     */
    @Override
    public List<OrganizeEntity> getAllCompanyList(String keyWord) {
        return null;
    }

    /**
     * 列表
     *
     * @param id
     * @return
     */
    @Override
    public List<OrganizeEntity> getParentIdList(String id) {
        return null;
    }

    /**
     * 列表
     *
     * @return
     */
    @Override
    public List<OrganizeEntity> getList() {
        return Collections.emptyList();
    }

    /**
     * 列表(有效的组织)
     *
     * @param enable
     * @return
     */
    @Override
    public List<OrganizeEntity> getListById(Boolean enable) {
        return null;
    }

    /**
     * 列表
     *
     * @param fullName 组织名称
     * @return
     */
    @Override
    public OrganizeEntity getIdListByFullName(String fullName) {
        return null;
    }

    /**
     * 列表
     *
     * @param keyword
     * @return
     */
    @Override
    public List<OrganizeEntity> getList(String keyword) {
        return null;
    }

    /**
     * 获取部门名列表
     *
     * @param idList
     * @param enable
     * @return
     */
    @Override
    public List<OrganizeEntity> getOrgEntityList(List<String> idList, Boolean enable) {
        return null;
    }

    @Override
    public List<OrganizeEntity> getOrgEntityList(Set<String> orgList) {
        return null;
    }

    @Override
    public Map<String, Object> getOrgNameAndId(String s) {
        return null;
    }

    @Override
    public Map<String, Object> getOrgEncodeAndName(String department) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<OrganizeEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<OrganizeEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<OrganizeEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(OrganizeEntity entity) {
        return false;
    }

    @Override
    public OrganizeEntity getOne(Wrapper<OrganizeEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<OrganizeEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<OrganizeEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<OrganizeEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<OrganizeEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<OrganizeEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<OrganizeEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(OrganizeEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public OrganizeEntity getOneIgnoreLogic(Wrapper<OrganizeEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<OrganizeEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<OrganizeEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
