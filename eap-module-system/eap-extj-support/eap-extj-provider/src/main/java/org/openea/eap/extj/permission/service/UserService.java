package org.openea.eap.extj.permission.service;

import org.openea.eap.extj.base.Page;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.model.user.mod.UserConditionModel;
import org.openea.eap.extj.permission.model.user.page.PaginationUser;
import org.openea.eap.extj.permission.model.user.vo.UserByRoleVO;
import org.openea.eap.extj.permission.model.user.vo.UserExportVO;
import org.openea.eap.extj.permission.model.user.vo.UserImportVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户信息
 *
 * 
 */
public interface UserService extends SuperService<UserEntity> {

    /*======================get接口========================*/

    List<UserEntity> getAdminList();

    /**
     * 列表
     *
     * @param pagination 条件
     * @return
     */
    List<UserEntity>  getList(Pagination pagination, String organizeId, Boolean flag, Boolean filter);

    /**
     * 列表
     *
     * @param pagination 条件
     * @param filterCurrentUser
     * @return
     */
    List<UserEntity>  getList(Pagination pagination, Boolean filterCurrentUser);

    /**
     * 通过关键字查询
     *
     * @param pagination
     * @return
     */
    List<UserEntity> getUserPage(Pagination pagination);

    /**
     * 通过组织id获取用户列表
     *
     * @param organizeId 组织id
     * @param keyword    关键字
     * @return
     */
    List<UserEntity> getListByOrganizeId(String organizeId, String keyword);

    /**
     * 列表
     *
     * @return
     */
    List<UserEntity> getList();

    /**
     * 用户名列表（在线开发）
     *
     * @param idList
     * @return
     */
    List<UserEntity> getUserNameList(List<String> idList);

    /**
     * 用户名列表（在线开发）
     *
     * @param idList
     * @return
     */
    List<UserEntity> getUserNameList(Set<String> idList);


    /**
     * （id : name/account）
     * @return
     */
    Map<String,Object> getUserMap();


    /**
     * （ name/account: id）
     * @return
     */
    Map<String,Object> getUserNameAndIdMap();

    /**
     * 通过名称查询id
     *
     * @return
     */
    UserEntity getByRealName(String realName);


    /**
     * 通过名称查询id
     *
     * @return
     */
    UserEntity getByRealName(String realName,String account);

    /**
     * 列表
     *
     * @param managerId 主管Id
     * @param keyword   关键字
     * @return
     */
    List<UserEntity> getListByManagerId(String managerId, String keyword);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    UserEntity getInfo(String id);

    /**
     * 信息
     *
     * @param account 账户
     * @return
     */
    UserEntity getUserByAccount(String account);
    /**
     * 信息
     *
     * @param mobile 手机号码
     * @return
     */
    UserEntity getUserByMobile(String mobile);

    /*==============================================*/

    Boolean setAdminListByIds(List<String> adminIds);

    /**
     * 验证账户
     *
     * @param account 账户
     * @return
     */
    boolean isExistByAccount(String account);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    Boolean create(UserEntity entity) throws Exception;

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    Boolean update(String id, UserEntity entity) throws Exception;

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(UserEntity entity);

    /**
     * 修改密码
     *
     * @param entity 实体对象
     */
    void updatePassword(UserEntity entity);

    /**
     * 查询用户名称
     *
     * @param id 主键值
     * @return
     */
    List<UserEntity> getUserName(List<String> id);

    /**
     * 查询用户名称
     *
     * @param id 主键值
     * @return
     */
    List<UserEntity> getListByUserIds(List<String> id);

    /**
     * 查询出分页被禁用的账号
     *
     * @param id 主键值
     * @return
     */
    List<UserEntity> getUserList(List<String> id);

    /**
     * 通过account返回user实体
     *
     * @param account 账户
     * @return
     */
    UserEntity getUserEntity(String account);

    /**
     * 获取用户id
     *
     * @return
     */
    List<String> getListId();

    /**
     * 添加岗位或角色成员
     *
     * @param entity
     */
    void update(UserEntity entity, String type);

    /**
     * 添加岗位或角色成员
     *
     * @param entity
     */
    void updateLastTime(UserEntity entity, String type);

    /**
     * 判断是否为自己的下属
     *
     * @param id
     * @param managerId
     * @return
     */
    boolean isSubordinate(String id, String managerId);

    /**
     * 导出Excel
     *
     * @param dataType
     * @param selectKey
     * @param pagination
     * @return
     */
    DownloadVO exportExcel(String dataType, String selectKey, PaginationUser pagination);

    /**
     * 导入预览
     *
     * @param personList
     * @return
     */
    Map<String, Object> importPreview(List<UserExportVO> personList);

    /**
     * 导入数据
     *
     * @param dataList 数据源
     */
    UserImportVO importData(List<UserExportVO> dataList);

    /**
     * 通过组织id获取上级id集合
     *
     * @param organizeId
     * @param organizeParentIdList
     */
    void getOrganizeIdTree(String organizeId, StringBuffer organizeParentIdList);

    /**
     * 导出错误报告
     *
     * @param dataList
     * @return
     */
    DownloadVO exportExceptionData(List<UserExportVO> dataList);

    /**
     * 候选人分页查询
     *
     * @param id
     * @param pagination
     * @return
     */
    List<UserEntity> getUserName(List<String> id, Pagination pagination);

    /**
     * 候选人分页查询
     *
     * @param id
     * @param pagination
     * @param flag 是否过滤自己
     * @return
     */
    List<UserEntity> getUserNames(List<String> id, Pagination pagination, Boolean flag, Boolean enabledMark);

    /**
     * 根据角色ID获取所在组织下的所有成员
     * @param roleId 角色ID
     * @return
     */
    List<UserEntity> getListByRoleId(String roleId);


    /**
     * 删除在线的角色用户
     */
    Boolean delCurRoleUser(List<String> objectIdAll);

    /**
     * 删除在线用户
     * @param userIds 用户IDs
     * @return 执行结果
     */
    Boolean delCurUser(String... userIds);

    /**
     * 获取用户信息
     *
     *
     * @param orgIdList
     * @param keyword
     * @return
     */
    List<UserEntity> getList(List<String> orgIdList, String keyword);

    /**
     * 得到用户关系
     *
     * @param userIds
     * @return
     */
    List<String> getUserIdList(List<String> userIds);

    /**
     * 获取用户下拉框列表
     */
    List<UserByRoleVO> getListByAuthorize(String organizeId, Page page);

    /**
     * 查询给定的条件是否有默认当前登录者的默认用户值
     * @param userConditionModel
     * @return
     */
    String getDefaultCurrentValueUserId(UserConditionModel userConditionModel);

    List<UserEntity> getListByRoleIds(List<String> roleIds);

}
