package org.openea.eap.module.infra.service.file;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleCreateReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModulePageReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleUpdateReqVO;
import org.openea.eap.module.infra.dal.dataobject.file.FileModuleDO;

import javax.validation.Valid;

public interface FileModuleService {

    /**
     * 创建文件模块
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFileModule(@Valid FileModuleCreateReqVO createReqVO);

    /**
     * 更新文件模块
     *
     * @param updateReqVO 更新信息
     */
    void updateFileModule(@Valid FileModuleUpdateReqVO updateReqVO);


    /**
     * 删除文件模块
     *
     * @param id 编号
     */
    void deleteFileModule(Long id);

    /**
     * 获得文件模块
     *
     * @param id 编号
     * @return 文件模块
     */
    FileModuleDO getFileModule(Long id);

    /**
     * 获得文件模块分页
     *
     * @param pageReqVO 分页查询
     * @return 文件模块分页
     */
    PageResult<FileModuleDO> getFileModulePage(FileModulePageReqVO pageReqVO);
}
