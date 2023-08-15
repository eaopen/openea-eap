package org.openea.eap.module.system.dal.mysql.language;

import java.util.*;

import cn.hutool.core.collection.CollectionUtil;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.openea.eap.framework.mybatis.core.mapper.BaseMapperX;
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.module.system.controller.admin.language.vo.*;

/**
 * 翻译 Mapper
 *
 * @author eap
 */
@Mapper
public interface I18nJsonDataMapper extends BaseMapperX<I18nJsonDataDO> {

    default I18nJsonDataDO queryI18nJsonDataByKey(String key) {
        List<I18nJsonDataDO> list = selectList(I18nJsonDataDO::getAlias, key);
        if(CollectionUtil.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    default PageResult<I18nJsonDataDO> selectPage(I18nJsonDataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<I18nJsonDataDO>()
                .likeIfPresent(I18nJsonDataDO::getModule, reqVO.getModule())
                .likeIfPresent(I18nJsonDataDO::getAlias, reqVO.getAlias())
                .likeIfPresent(I18nJsonDataDO::getName, reqVO.getName())
                .likeIfPresent(I18nJsonDataDO::getJson, reqVO.getJson())
                .betweenIfPresent(I18nJsonDataDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(I18nJsonDataDO::getId));
    }

    default List<I18nJsonDataDO> selectList(I18nJsonDataExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<I18nJsonDataDO>()
                .likeIfPresent(I18nJsonDataDO::getModule, reqVO.getModule())
                .likeIfPresent(I18nJsonDataDO::getAlias, reqVO.getAlias())
                .likeIfPresent(I18nJsonDataDO::getName, reqVO.getName())
                .likeIfPresent(I18nJsonDataDO::getJson, reqVO.getJson())
                .betweenIfPresent(I18nJsonDataDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(I18nJsonDataDO::getId));
    }

}
