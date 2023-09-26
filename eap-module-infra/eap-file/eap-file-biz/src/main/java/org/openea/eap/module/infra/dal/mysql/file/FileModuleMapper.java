package org.openea.eap.module.infra.dal.mysql.file;

import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.mybatis.core.mapper.BaseMapperX;
import org.openea.eap.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModulePageReqVO;
import org.openea.eap.module.infra.dal.dataobject.file.FileModuleDO;

@Mapper
public interface FileModuleMapper extends BaseMapperX<FileModuleDO> {

    default PageResult<FileModuleDO> selectPage(FileModulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileModuleDO>()
                .likeIfPresent(FileModuleDO::getCode, reqVO.getCode())
                .likeIfPresent(FileModuleDO::getName, reqVO.getName())
                .betweenIfPresent(FileModuleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileModuleDO::getId));
    }

}
