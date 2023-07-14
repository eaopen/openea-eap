package org.openea.eap.module.system.convert.language;

import java.util.*;

import org.openea.eap.framework.common.pojo.PageResult;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.openea.eap.module.system.controller.admin.language.vo.*;
import org.openea.eap.module.system.dal.dataobject.language.LangTypeDO;

/**
 * 语言 Convert
 *
 * @author eap
 */
@Mapper
public interface LangTypeConvert {

    LangTypeConvert INSTANCE = Mappers.getMapper(LangTypeConvert.class);

    LangTypeDO convert(LangTypeCreateReqVO bean);

    LangTypeDO convert(LangTypeUpdateReqVO bean);

    LangTypeRespVO convert(LangTypeDO bean);

    List<LangTypeRespVO> convertList(List<LangTypeDO> list);

    PageResult<LangTypeRespVO> convertPage(PageResult<LangTypeDO> page);

    List<LangTypeExcelVO> convertList02(List<LangTypeDO> list);

}
