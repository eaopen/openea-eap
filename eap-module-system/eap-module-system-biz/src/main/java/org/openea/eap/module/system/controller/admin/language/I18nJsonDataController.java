package org.openea.eap.module.system.controller.admin.language;

import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
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
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.openea.eap.module.system.convert.language.I18nJsonDataConvert;
import org.openea.eap.module.system.service.language.I18nJsonDataService;

@Tag(name = "管理后台 - 翻译")
@RestController
@RequestMapping("/system/I18n-json-data")
@Validated
public class I18nJsonDataController {

    @Resource
    private I18nJsonDataService i18nJsonDataService;

    @PostMapping("/create")
    @Operation(summary = "创建翻译")
    @PreAuthorize("@ss.hasPermission('system:I18n-json-data:create')")
    public CommonResult<Long> createI18nJsonData(@Valid @RequestBody I18nJsonDataCreateReqVO createReqVO) {
        return success(i18nJsonDataService.createI18nJsonData(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新翻译")
    @PreAuthorize("@ss.hasPermission('system:I18n-json-data:update')")
    public CommonResult<Boolean> updateI18nJsonData(@Valid @RequestBody I18nJsonDataUpdateReqVO updateReqVO) {
        // format json, 恢复单行字符串
        if(StringUtils.isNotEmpty(updateReqVO.getJson())){
            updateReqVO.setJson(JSONUtil.parseObj(updateReqVO.getJson()).toString());
        }
        i18nJsonDataService.updateI18nJsonData(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除翻译")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:I18n-json-data:delete')")
    public CommonResult<Boolean> deleteI18nJsonData(@RequestParam("id") Long id) {
        i18nJsonDataService.deleteI18nJsonData(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得翻译")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:I18n-json-data:query')")
    public CommonResult<I18nJsonDataRespVO> getI18nJsonData(@RequestParam("id") Long id) {
        I18nJsonDataDO i18nJsonData = i18nJsonDataService.getI18nJsonData(id);
        // format json
        if(StringUtils.isNotEmpty(i18nJsonData.getJson())){
            i18nJsonData.setJson(JSONUtil.parseObj(i18nJsonData.getJson()).toJSONString(2));
        }
        return success(I18nJsonDataConvert.INSTANCE.convert(i18nJsonData));
    }

    @GetMapping("/list")
    @Operation(summary = "获得翻译列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('system:I18n-json-data:query')")
    public CommonResult<List<I18nJsonDataRespVO>> getI18nJsonDataList(@RequestParam("ids") Collection<Long> ids) {
        List<I18nJsonDataDO> list = i18nJsonDataService.getI18nJsonDataList(ids);
        return success(I18nJsonDataConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得翻译分页")
    @PreAuthorize("@ss.hasPermission('system:I18n-json-data:query')")
    public CommonResult<PageResult<I18nJsonDataRespVO>> getI18nJsonDataPage(@Valid I18nJsonDataPageReqVO pageVO) {
        PageResult<I18nJsonDataDO> pageResult = i18nJsonDataService.getI18nJsonDataPage(pageVO);
        return success(I18nJsonDataConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出翻译 Excel")
    @PreAuthorize("@ss.hasPermission('system:I18n-json-data:export')")
    @OperateLog(type = EXPORT)
    public void exportI18nJsonDataExcel(@Valid I18nJsonDataExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<I18nJsonDataDO> list = i18nJsonDataService.getI18nJsonDataList(exportReqVO);
        // 导出 Excel
        List<I18nJsonDataExcelVO> datas = I18nJsonDataConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "翻译.xls", "数据", I18nJsonDataExcelVO.class, datas);
    }

}
