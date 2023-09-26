package org.openea.eap.module.infra.controller.admin.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.framework.common.pojo.CommonResult;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleCreateReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModulePageReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleRespVO;
import org.openea.eap.module.infra.controller.admin.file.vo.module.FileModuleUpdateReqVO;
import org.openea.eap.module.infra.convert.file.FileModuleConvert;
import org.openea.eap.module.infra.dal.dataobject.file.FileModuleDO;
import org.openea.eap.module.infra.service.file.FileModuleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static org.openea.eap.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 文件模块")
@RestController
@RequestMapping("/infra/file-module")
@Validated
public class FileModuleController {

    @Resource
    private FileModuleService fileModuleService;

    @PostMapping("/create")
    @Operation(summary = "创建文件模块")
    @PreAuthorize("@ss.hasPermission('infra:file-module:create')")
    public CommonResult<Long> createFileModule(@Valid @RequestBody FileModuleCreateReqVO createReqVO) {
        return success(fileModuleService.createFileModule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新文件模块")
    @PreAuthorize("@ss.hasPermission('infra:file-module:update')")
    public CommonResult<Boolean> updateFileModule(@Valid @RequestBody FileModuleUpdateReqVO updateReqVO) {
        fileModuleService.updateFileModule(updateReqVO);
        return success(true);
    }



    @DeleteMapping("/delete")
    @Operation(summary = "删除文件模块")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file-module:delete')")
    public CommonResult<Boolean> deleteFileModule(@RequestParam("id") Long id) {
        fileModuleService.deleteFileModule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得文件模块")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:file-module:query')")
    public CommonResult<FileModuleRespVO> getFileModule(@RequestParam("id") Long id) {
        FileModuleDO fileModule = fileModuleService.getFileModule(id);
        return success(FileModuleConvert.INSTANCE.convert(fileModule));
    }

    @GetMapping("/page")
    @Operation(summary = "获得文件模块分页")
    @PreAuthorize("@ss.hasPermission('infra:file-module:query')")
    public CommonResult<PageResult<FileModuleRespVO>> getFileModulePage(@Valid FileModulePageReqVO pageVO) {
        PageResult<FileModuleDO> pageResult = fileModuleService.getFileModulePage(pageVO);
        return success(FileModuleConvert.INSTANCE.convertPage(pageResult));
    }
}
