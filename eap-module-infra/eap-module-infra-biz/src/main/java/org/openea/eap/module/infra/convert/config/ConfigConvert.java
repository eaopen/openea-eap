package org.openea.eap.module.infra.convert.config;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.infra.controller.admin.config.vo.ConfigCreateReqVO;
import org.openea.eap.module.infra.controller.admin.config.vo.ConfigExcelVO;
import org.openea.eap.module.infra.controller.admin.config.vo.ConfigRespVO;
import org.openea.eap.module.infra.controller.admin.config.vo.ConfigUpdateReqVO;
import org.openea.eap.module.infra.dal.dataobject.config.ConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConfigConvert {

    ConfigConvert INSTANCE = Mappers.getMapper(ConfigConvert.class);

    PageResult<ConfigRespVO> convertPage(PageResult<ConfigDO> page);

    @Mapping(source = "configKey", target = "key")
    ConfigRespVO convert(ConfigDO bean);

    @Mapping(source = "key", target = "configKey")
    ConfigDO convert(ConfigCreateReqVO bean);

    ConfigDO convert(ConfigUpdateReqVO bean);

    @Mapping(source = "configKey", target = "key")
    List<ConfigExcelVO> convertList(List<ConfigDO> list);

}
