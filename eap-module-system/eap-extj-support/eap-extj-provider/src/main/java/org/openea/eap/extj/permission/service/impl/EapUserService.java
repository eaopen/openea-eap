package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.Page;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.model.user.mod.UserConditionModel;
import org.openea.eap.extj.permission.model.user.page.PaginationUser;
import org.openea.eap.extj.permission.model.user.vo.UserByRoleVO;
import org.openea.eap.extj.permission.model.user.vo.UserExportVO;
import org.openea.eap.extj.permission.model.user.vo.UserImportVO;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;

/**
 * todo eap
 */
@Service
@Slf4j
public class EapUserService implements UserService {

    @Resource
    private AdminUserService adminUserService;
    private PageResult<AdminUserDO> userList;

    private UserEntity covertUser(AdminUserDO adminUser){
        UserEntity user = new UserEntity();
        user.setId(""+adminUser.getId());
        user.setAccount(adminUser.getUsername());
        user.setRealName(adminUser.getNickname());
        user.setHeadIcon(adminUser.getAvatar());
        user.setEnabledMark(1);
        return user;
    }

    @Override
    public UserEntity getInfo(String userId) {
        try{
            AdminUserDO adminUserDO = adminUserService.getUser(new Long(userId));
            return covertUser(adminUserDO);
        }catch (Throwable t){
            log.warn(t.getMessage());
        }
        return null;
    }

    /**
     * 信息
     *
     * @param account 账户
     * @return
     */
    @Override
    public UserEntity getUserByAccount(String account) {
        return null;
    }

    /**
     * 信息
     *
     * @param mobile 手机号码
     * @return
     */
    @Override
    public UserEntity getUserByMobile(String mobile) {
        return null;
    }

    @Override
    public Boolean setAdminListByIds(List<String> adminIds) {
        return null;
    }

    /**
     * 验证账户
     *
     * @param account 账户
     * @return
     */
    @Override
    public boolean isExistByAccount(String account) {
        return false;
    }

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    @Override
    public Boolean create(UserEntity entity) throws Exception {
        return null;
    }

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    @Override
    public Boolean update(String id, UserEntity entity) throws Exception {
        return null;
    }

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    @Override
    public void delete(UserEntity entity) {

    }

    /**
     * 修改密码
     *
     * @param entity 实体对象
     */
    @Override
    public void updatePassword(UserEntity entity) {

    }

    @Override
    public List<UserEntity> getUserName(List<String> collect) {
        return Collections.emptyList();
    }

    /**
     * 查询用户名称
     *
     * @param id 主键值
     * @return
     */
    @Override
    public List<UserEntity> getListByUserIds(List<String> id) {
        return null;
    }

    /**
     * 查询出分页被禁用的账号
     *
     * @param id 主键值
     * @return
     */
    @Override
    public List<UserEntity> getUserList(List<String> id) {
        return null;
    }

    /**
     * 通过account返回user实体
     *
     * @param account 账户
     * @return
     */
    @Override
    public UserEntity getUserEntity(String account) {
        return null;
    }

    @Override
    public List<String> getUserIdList(List<String> userIds) {
        return Collections.emptyList();
    }

    /**
     * 获取用户下拉框列表
     *
     * @param organizeId
     * @param page
     */
    @Override
    public List<UserByRoleVO> getListByAuthorize(String organizeId, Page page) {
        return null;
    }

    /**
     * 查询给定的条件是否有默认当前登录者的默认用户值
     *
     * @param userConditionModel
     * @return
     */
    @Override
    public String getDefaultCurrentValueUserId(UserConditionModel userConditionModel) {
        return null;
    }

    @Override
    public List<UserEntity> getListByRoleIds(List<String> roleIds) {
        return null;
    }

    @Override
    public List<String> getListId() {
        return  Collections.emptyList();
    }

    /**
     * 添加岗位或角色成员
     *
     * @param entity
     * @param type
     */
    @Override
    public void update(UserEntity entity, String type) {

    }

    /**
     * 添加岗位或角色成员
     *
     * @param entity
     * @param type
     */
    @Override
    public void updateLastTime(UserEntity entity, String type) {

    }

    /**
     * 判断是否为自己的下属
     *
     * @param id
     * @param managerId
     * @return
     */
    @Override
    public boolean isSubordinate(String id, String managerId) {
        return false;
    }

    /**
     * 导出Excel
     *
     * @param dataType
     * @param selectKey
     * @param pagination
     * @return
     */
    @Override
    public DownloadVO exportExcel(String dataType, String selectKey, PaginationUser pagination) {
        return null;
    }

    /**
     * 导入预览
     *
     * @param personList
     * @return
     */
    @Override
    public Map<String, Object> importPreview(List<UserExportVO> personList) {
        return null;
    }

    /**
     * 导入数据
     *
     * @param dataList 数据源
     */
    @Override
    public UserImportVO importData(List<UserExportVO> dataList) {
        return null;
    }

    /**
     * 通过组织id获取上级id集合
     *
     * @param organizeId
     * @param organizeParentIdList
     */
    @Override
    public void getOrganizeIdTree(String organizeId, StringBuffer organizeParentIdList) {

    }

    /**
     * 导出错误报告
     *
     * @param dataList
     * @return
     */
    @Override
    public DownloadVO exportExceptionData(List<UserExportVO> dataList) {
        return null;
    }

    /**
     * 候选人分页查询
     *
     * @param id
     * @param pagination
     * @return
     */
    @Override
    public List<UserEntity> getUserName(List<String> id, Pagination pagination) {
        return null;
    }

    /**
     * 候选人分页查询
     *
     * @param id
     * @param pagination
     * @param flag        是否过滤自己
     * @param enabledMark
     * @return
     */
    @Override
    public List<UserEntity> getUserNames(List<String> id, Pagination pagination, Boolean flag, Boolean enabledMark) {
        return null;
    }

    /**
     * 根据角色ID获取所在组织下的所有成员
     *
     * @param roleId 角色ID
     * @return
     */
    @Override
    public List<UserEntity> getListByRoleId(String roleId) {
        return null;
    }

    /**
     * 删除在线的角色用户
     *
     * @param objectIdAll
     */
    @Override
    public Boolean delCurRoleUser(List<String> objectIdAll) {
        return null;
    }

    /**
     * 删除在线用户
     *
     * @param userIds 用户IDs
     * @return 执行结果
     */
    @Override
    public Boolean delCurUser(String... userIds) {
        return null;
    }

    /**
     * 获取用户信息
     *
     * @param orgIdList
     * @param keyword
     * @return
     */
    @Override
    public List<UserEntity> getList(List<String> orgIdList, String keyword) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<UserEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<UserEntity> entityList, int batchSize) {
        return false;
    }


    @Override
    public boolean updateBatchById(Collection<UserEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(UserEntity entity) {
        return false;
    }

    @Override
    public UserEntity getOne(Wrapper<UserEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<UserEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<UserEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public Map<String, Object> getUserMap() {
        return null;
    }

    @Override
    public List<UserEntity> getAdminList() {
        return null;
    }

    /**
     * 列表
     *
     * @param pagination 条件
     * @param organizeId
     * @param flag
     * @param filter
     * @return
     */
    @Override
    public List<UserEntity> getList(Pagination pagination, String organizeId, Boolean flag, Boolean filter) {
        return null;
    }

    /**
     * 列表
     *
     * @param pagination        条件
     * @param filterCurrentUser
     * @return
     */
    @Override
    public List<UserEntity> getList(Pagination pagination, Boolean filterCurrentUser) {
        PageResult<AdminUserDO> userList = adminUserService.getUserList(pagination, filterCurrentUser);
        List<AdminUserDO> list = userList.getList();
        List<UserEntity> userEntityList=new ArrayList<>();
        for (AdminUserDO adminUserDO : list) {
            userEntityList.add(covertUser(adminUserDO));
        }
        return userEntityList;
    }

    /**
     * 通过关键字查询
     *
     * @param pagination
     * @return
     */
    @Override
    public List<UserEntity> getUserPage(Pagination pagination) {
        return null;
    }

    /**
     * 通过组织id获取用户列表
     *
     * @param organizeId 组织id
     * @param keyword    关键字
     * @return
     */
    @Override
    public List<UserEntity> getListByOrganizeId(String organizeId, String keyword) {
        return null;
    }

    /**
     * 列表
     *
     * @return
     */
    @Override
    public List<UserEntity> getList() {
        return null;
    }

    /**
     * 用户名列表（在线开发）
     *
     * @param idList
     * @return
     */
    @Override
    public List<UserEntity> getUserNameList(List<String> idList) {
        return null;
    }

    @Override
    public List<UserEntity> getUserNameList(Set<String> userList) {
        return  Collections.emptyList();
    }

    @Override
    public Map<String, Object> getUserNameAndIdMap() {
        return null;
    }

    /**
     * 通过名称查询id
     *
     * @param realName
     * @return
     */
    @Override
    public UserEntity getByRealName(String realName) {
        return null;
    }

    /**
     * 通过名称查询id
     *
     * @param realName
     * @param account
     * @return
     */
    @Override
    public UserEntity getByRealName(String realName, String account) {
        return null;
    }

    /**
     * 列表
     *
     * @param managerId 主管Id
     * @param keyword   关键字
     * @return
     */
    @Override
    public List<UserEntity> getListByManagerId(String managerId, String keyword) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<UserEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<UserEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<UserEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<UserEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(UserEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public UserEntity getOneIgnoreLogic(Wrapper<UserEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<UserEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<UserEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
