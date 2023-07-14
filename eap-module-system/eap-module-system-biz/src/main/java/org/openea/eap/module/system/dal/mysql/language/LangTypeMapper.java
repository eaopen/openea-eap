package org.openea.eap.module.system.dal.mysql.language;

import java.util.*;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.openea.eap.framework.mybatis.core.mapper.BaseMapperX;
import org.openea.eap.module.system.dal.dataobject.language.LangTypeDO;
import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.module.system.controller.admin.language.vo.*;

/**
 * 语言 Mapper
 *
 * @author eap
 */
@Mapper
public interface LangTypeMapper extends BaseMapperX<LangTypeDO> {

    default PageResult<LangTypeDO> selectPage(LangTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LangTypeDO>()
                .likeIfPresent(LangTypeDO::getAlias, reqVO.getAlias())
                .likeIfPresent(LangTypeDO::getName, reqVO.getName())
                .betweenIfPresent(LangTypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LangTypeDO::getId));
    }

    default List<LangTypeDO> selectList(LangTypeExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<LangTypeDO>()
                .likeIfPresent(LangTypeDO::getAlias, reqVO.getAlias())
                .likeIfPresent(LangTypeDO::getName, reqVO.getName())
                .betweenIfPresent(LangTypeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LangTypeDO::getId));
    }

}
