package org.openea.eap.module.infra.dal.mysql.file;

import org.apache.ibatis.annotations.Select;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.mybatis.core.mapper.BaseMapperX;
import org.openea.eap.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.openea.eap.module.infra.controller.admin.file.vo.config.FileConfigPageReqVO;
import org.openea.eap.module.infra.dal.dataobject.file.FileConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface FileConfigMapper extends BaseMapperX<FileConfigDO> {

    default PageResult<FileConfigDO> selectPage(FileConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileConfigDO>()
                .likeIfPresent(FileConfigDO::getName, reqVO.getName())
                .eqIfPresent(FileConfigDO::getStorage, reqVO.getStorage())
                .betweenIfPresent(FileConfigDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileConfigDO::getId));
    }

    default FileConfigDO selectByMaster(){
        return selectOne(FileConfigDO::getMaster, true);
    }


}
