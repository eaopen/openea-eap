package org.openea.eap.module.infra.convert.file;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleCreateReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleRespVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleUpdateReqVO;
import org.openea.eap.module.infra.dal.dataobject.file.FileModuleDO;

import java.util.List;

/**
 * 文件模块 Convert
 *
 */
@Mapper
public interface FileModuleConvert {

    FileModuleConvert INSTANCE = Mappers.getMapper(FileModuleConvert.class);

    FileModuleDO convert(FileModuleCreateReqVO bean);

    FileModuleDO convert(FileModuleUpdateReqVO bean);

    FileModuleRespVO convert(FileModuleDO bean);

    List<FileModuleRespVO> convertList(List<FileModuleDO> list);

    PageResult<FileModuleRespVO> convertPage(PageResult<FileModuleDO> page);

}
