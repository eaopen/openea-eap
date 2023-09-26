package org.openea.eap.module.obpm.service.obpm.sync;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONObject;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.framework.common.util.collection.CollectionUtils;
import org.openea.eap.framework.common.util.spring.EapAppUtil;
import org.openea.eap.module.obpm.service.obpm.ObmpClientService;
import org.openea.eap.module.obpm.service.obpm.ObpmUserServiceImpl;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.dal.mysql.user.AdminUserMapper;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * 同步组织（部门和岗位）
 * 同步角色以及权限数据
 * 同步用户以及分配关系
 *
 */
@Service
@Slf4j
public class ObpmOrgSyncService implements InitializingBean {

    @Resource
    private AdminUserMapper userMapper;

    @Resource
    private AdminUserService userService;

    @Resource
    private ObpmUserServiceImpl obpmUserService;


    @Override
    public void afterPropertiesSet() throws Exception {
        syncObpmAll();
    }

    @Async
    @Synchronized
    public Integer syncObpmAll(){
        return syncObpm(null);
    }

    public Integer syncObpm(Date lastSyncTime){
//        if(lastSyncTime==null){
//            lastSyncTime = DateUtil.lastWeek();
//        }
        log.info("sync obpm start ");
        int count = 0;
        try{
            count += syncOrg(lastSyncTime);
            count += syncRole(lastSyncTime);
            count += syncUser(lastSyncTime);
        }catch (Throwable t){
            log.warn("syncObpm fail", t);
        }

        log.info("sync obpm end, add/update total count="+count);
        return count;
    }

    /**
     * 同步组织信息（公司/部门/岗位）
     * @param lastSyncTime
     * @return
     */
    public int syncOrg(Date lastSyncTime){
        return 0;
    }

    /**
     * 同步角色及权限信息
     * @param lastSyncTime
     * @return
     */
    public int syncRole(Date lastSyncTime){
        return 0;
    }

    /**
     * 同步用户数据
     * @param lastSyncTime 上次同步时间
     * @return 全量用户数据或上次同步时间之后更新的数据
     */
    public int syncUser(Date lastSyncTime){
        int count = 0;
        // 唯一标识 username
        // 密码 password 加密后密码
        // 状态 status 是否启用
        List<JSONObject> listUser = getObpmUsers(lastSyncTime);
        if(CollectionUtils.isAnyEmpty(listUser)){
            return count;
        }
        log.debug("getObpmUsers: list size="+listUser.size());
        for(JSONObject jsonUser: listUser){
            String username = jsonUser.getStr("account");
            if(ObjectUtils.isEmpty(username)){
                username = jsonUser.getStr("username");
            }
            if(ObjectUtils.isEmpty(username)) continue;
            // 排除eap的管理员用户账号
            if("admin".equals(username)){
                continue;
            }
            AdminUserDO userDo = userMapper.selectByUsername(username);
            if(userDo==null){
                // 新建用户，同步密码
                // TODO 用户默认启用，是否初始化角色用于登录？
                userDo = obpmUserService.createAdminUser(jsonUser);
                count++;
            }else{
                // 更新用户，更新状态，不更新密码
                // 是否要对比更新时间，用于只更新比eap中更新时间更新的记录
                boolean needUpdate = false;
                // 检查用户状态
                boolean obpmStatus = jsonUser.getBool("status", true);
                if(obpmStatus && userDo.getStatus().equals(CommonStatusEnum.DISABLE)){
                    //重新启用
                    userDo.setStatus(CommonStatusEnum.ENABLE.getStatus());
                    needUpdate = true;
                }else if(!obpmStatus && userDo.getStatus().equals(CommonStatusEnum.ENABLE)){
                    //关闭
                    userDo.setStatus(CommonStatusEnum.DISABLE.getStatus());
                    needUpdate = true;
                }
                // TODO 其他检查
                // 更新用户
                if(needUpdate){
                    userDo.setUpdateTime(LocalDateTimeUtil.now());
                    userDo.setUpdater("obpm-sync");
                    count += userMapper.updateById(userDo);
                }
            }
        }
        return count;
    }

    private List<JSONObject> getObpmUsers(Date lastSyncTime){
        ObmpClientService obmpClientService = EapAppUtil.getBean(ObmpClientService.class);
        if(obmpClientService==null){
            log.warn("queryObpmListData fail: obmpClientService is null");
            return null;
        }
        JdbcTemplate obpmJdbcTemplate = obmpClientService.getObpmJdbcTemplate();
        if(obpmJdbcTemplate!=null){
            return getObpmUsers2(lastSyncTime);
        }
        return obmpClientService.queryUserList(lastSyncTime);
    }

    private List<JSONObject> getObpmUsers2(Date lastSyncTime){
        String queryUserSql = "select id_ as id, account_ as username, fullname_ as fullname, password_ as password" +
                ", email_ as email, mobile_ as mobile, sex_ as sex" +
                ", status_ as status" +
                " from org_user u ";
        if(lastSyncTime!=null){
            queryUserSql += " where u.update_time_>"+ DateUtil.formatDateTime(lastSyncTime)
            + " or u.create_time>"+ DateUtil.formatDateTime(lastSyncTime)
            + " or (u.create_time is null and u.update_time is null) ";
        }
        List<JSONObject> listUser = queryObpmListData(queryUserSql);
        return listUser;
    }

    private List<JSONObject> queryObpmListData(String querySql){
        if(ObjectUtils.isEmpty(querySql)){
            return null;
        }
        ObmpClientService obmpClientService = EapAppUtil.getBean(ObmpClientService.class);
        if(obmpClientService==null){
            log.warn("queryObpmListData fail: obmpClientService is null");
            return null;
        }
        JdbcTemplate obpmJdbcTemplate = obmpClientService.getObpmJdbcTemplate();
        if(obpmJdbcTemplate==null){
            log.warn("queryObpmListData fail: obpmJdbcTemplate is null");
            return null;
        }
        List<JSONObject> listData = null;
        try{
            listData = obpmJdbcTemplate.query(querySql, new RowMapper<JSONObject>() {
                @Override
                public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                    JSONObject jsonObj = new JSONObject();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        if(ObjectUtils.isEmpty(columnName)){
                            columnName = metaData.getColumnName(i);
                        }
                        Object value = rs.getObject(i);
                        jsonObj.set(columnName, value);
                    }
                    return jsonObj;
                }
            });
        }catch (Throwable t){
            log.warn("queryObpmListData fail: "+t.getMessage(), t);
        }
        return  listData;
    }

}
