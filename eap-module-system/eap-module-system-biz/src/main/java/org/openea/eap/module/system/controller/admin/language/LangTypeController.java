package org.openea.eap.module.system.controller.admin.language;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.common.pojo.CommonResult;
import static org.openea.eap.framework.common.pojo.CommonResult.success;

import org.openea.eap.framework.excel.core.util.ExcelUtils;

import org.openea.eap.framework.operatelog.core.annotations.OperateLog;
import static org.openea.eap.framework.operatelog.core.enums.OperateTypeEnum.*;

import org.openea.eap.module.system.controller.admin.language.vo.*;
import org.openea.eap.module.system.dal.dataobject.language.LangTypeDO;
import org.openea.eap.module.system.convert.language.LangTypeConvert;
import org.openea.eap.module.system.service.language.LangTypeService;

@Tag(name = "管理后台 - 语言")
@RestController
@RequestMapping("/system/lang-type")
@Validated
public class LangTypeController {

    @Resource
    private LangTypeService langTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建语言")
    @PreAuthorize("@ss.hasPermission('system:lang-type:create')")
    public CommonResult<Long> createLangType(@Valid @RequestBody LangTypeCreateReqVO createReqVO) {
        return success(langTypeService.createLangType(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新语言")
    @PreAuthorize("@ss.hasPermission('system:lang-type:update')")
    public CommonResult<Boolean> updateLangType(@Valid @RequestBody LangTypeUpdateReqVO updateReqVO) {
        langTypeService.updateLangType(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除语言")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:lang-type:delete')")
    public CommonResult<Boolean> deleteLangType(@RequestParam("id") Long id) {
        langTypeService.deleteLangType(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得语言")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:lang-type:query')")
    public CommonResult<LangTypeRespVO> getLangType(@RequestParam("id") Long id) {
        LangTypeDO langType = langTypeService.getLangType(id);
        return success(LangTypeConvert.INSTANCE.convert(langType));
    }

    @GetMapping("/list")
    @Operation(summary = "获得语言列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('system:lang-type:query')")
    public CommonResult<List<LangTypeRespVO>> getLangTypeList(@RequestParam("ids") Collection<Long> ids) {
        List<LangTypeDO> list = langTypeService.getLangTypeList(ids);
        return success(LangTypeConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得语言分页")
    @PreAuthorize("@ss.hasPermission('system:lang-type:query')")
    public CommonResult<PageResult<LangTypeRespVO>> getLangTypePage(@Valid LangTypePageReqVO pageVO) {
        PageResult<LangTypeDO> pageResult = langTypeService.getLangTypePage(pageVO);
        return success(LangTypeConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出语言 Excel")
    @PreAuthorize("@ss.hasPermission('system:lang-type:export')")
    @OperateLog(type = EXPORT)
    public void exportLangTypeExcel(@Valid LangTypeExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<LangTypeDO> list = langTypeService.getLangTypeList(exportReqVO);
        // 导出 Excel
        List<LangTypeExcelVO> datas = LangTypeConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "语言.xls", "数据", LangTypeExcelVO.class, datas);
    }

}
