package org.openea.eap.module.infra.service.file;

import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleCreateReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModulePageReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleUpdateReqVO;
import org.openea.eap.module.infra.convert.file.FileModuleConvert;
import org.openea.eap.module.infra.dal.dataobject.file.FileModuleDO;
import org.openea.eap.module.infra.dal.dataobject.file.FileModuleDO;
import org.openea.eap.module.infra.dal.mysql.file.FileModuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.module.infra.enums.ErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER;
import static org.openea.eap.module.infra.enums.ErrorCodeConstants.FILE_CONFIG_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class FileModuleServiceImpl implements FileModuleService{

    @Resource
    private FileModuleMapper fileModuleMapper;

    /**
     * 创建文件模块
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    @Override
    public Long createFileModule(FileModuleCreateReqVO createReqVO) {
        FileModuleDO fileModule = FileModuleConvert.INSTANCE.convert(createReqVO);
        fileModuleMapper.insert(fileModule);
        return fileModule.getId();
    }

    /**
     * 更新文件模块
     *
     * @param updateReqVO 更新信息
     */
    @Override
    public void updateFileModule(FileModuleUpdateReqVO updateReqVO) {
        // 校验存在
        FileModuleDO config = validateFileModuleExists(updateReqVO.getId());
        // 更新
        FileModuleDO updateObj = FileModuleConvert.INSTANCE.convert(updateReqVO);
        fileModuleMapper.updateById(updateObj);
    }

    /**
     * 删除文件模块
     *
     * @param id 编号
     */
    @Override
    public void deleteFileModule(Long id) {
        // 校验存在
        FileModuleDO config = validateFileModuleExists(id);
        // 删除
        fileModuleMapper.deleteById(id);
    }

    private FileModuleDO validateFileModuleExists(Long id) {
        FileModuleDO config = fileModuleMapper.selectById(id);
        if (config == null) {
            throw exception(FILE_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    /**
     * 获得文件模块
     *
     * @param id 编号
     * @return 文件模块
     */
    @Override
    public FileModuleDO getFileModule(Long id) {
        return fileModuleMapper.selectById(id);
    }

    /**
     * 获得文件模块分页
     *
     * @param pageReqVO 分页查询
     * @return 文件模块分页
     */
    @Override
    public PageResult<FileModuleDO> getFileModulePage(FileModulePageReqVO pageReqVO) {
        return fileModuleMapper.selectPage(pageReqVO);
    }
}
