package org.openea.eap.module.system.dal.mysql.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xingyuv.http.util.StringUtil;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.framework.common.pojo.PageParam;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.mybatis.core.mapper.BaseMapperX;
import org.openea.eap.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.openea.eap.module.system.controller.admin.user.vo.user.UserExportReqVO;
import org.openea.eap.module.system.controller.admin.user.vo.user.UserPageReqVO;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

import static org.openea.eap.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Mapper
public interface AdminUserMapper extends BaseMapperX<AdminUserDO> {

    default AdminUserDO selectByUsername(String username) {
        return selectOne(AdminUserDO::getUsername, username);
    }

    default AdminUserDO selectByEmail(String email) {
        return selectOne(AdminUserDO::getEmail, email);
    }

    default AdminUserDO selectByMobile(String mobile) {
        return selectOne(AdminUserDO::getMobile, mobile);
    }

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds) {
        if (StringUtil.isEmpty(reqVO.getKeyword())){
            return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserDO>()
                    .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                    .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                    .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                    .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                    .inIfPresent(AdminUserDO::getDeptId, deptIds)
                    .orderByDesc(AdminUserDO::getId));
        }else return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserDO>()
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)
                .orderByDesc(AdminUserDO::getId)
                .and(
                t->t.like(AdminUserDO::getUsername, reqVO.getKeyword())
                        .or().like(AdminUserDO::getMobile, reqVO.getKeyword())
                        .or().like(AdminUserDO::getStatus, reqVO.getKeyword())
                        .or().like(AdminUserDO::getNickname,reqVO.getKeyword())
        ));
    }

    default List<AdminUserDO> selectList(UserExportReqVO reqVO, Collection<Long> deptIds) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>()
                .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(AdminUserDO::getDeptId, deptIds));
    }

    default List<AdminUserDO> selectListByNickname(String nickname) {
        return selectList(new LambdaQueryWrapperX<AdminUserDO>().like(AdminUserDO::getNickname, nickname));
    }

    default List<AdminUserDO> selectListByStatus(Integer status) {
        return selectList(AdminUserDO::getStatus, status);
    }

    default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
        return selectList(AdminUserDO::getDeptId, deptIds);
    }

    default PageResult<AdminUserDO> selectListByPage(Pagination pagination, Boolean filterCurrentUser) {
        PageParam pageParam=new PageParam();
        pageParam.setPageNo((int)pagination.getCurrentPage());
        pageParam.setPageSize((int)pagination.getPageSize());
        LambdaQueryWrapperX<AdminUserDO> queryWrapper = new LambdaQueryWrapperX<>();
        if (filterCurrentUser) {
            String userId = String.valueOf(getLoginUserId());
            queryWrapper.ne(AdminUserDO::getId, userId);
        }
            return selectPage(pageParam, new LambdaQueryWrapperX<AdminUserDO>()
                    .orderByDesc(AdminUserDO::getId)
                    .and(
                            t -> t.like(AdminUserDO::getUsername, pagination.getKeyword())
                                    .or().like(AdminUserDO::getMobile, pagination.getKeyword())
                                    .or().like(AdminUserDO::getStatus, pagination.getKeyword())
                                    .or().like(AdminUserDO::getNickname, pagination.getKeyword())
                    ));

    }

}
