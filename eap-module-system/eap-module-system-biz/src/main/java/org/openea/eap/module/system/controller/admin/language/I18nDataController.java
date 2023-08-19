package org.openea.eap.module.system.controller.admin.language;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.framework.common.pojo.CommonResult;
import org.openea.eap.module.system.service.language.I18nDataService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static org.openea.eap.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - i18n国际化数据")
@RestController
@RequestMapping("/system/i18n-data")
@Validated
public class I18nDataController {

    @Resource
    private I18nDataService i18nDataService;

    @GetMapping(value = "getJs")
    @Operation(summary = "获得前端i18n json数据",description = "数据json格式 {language:{key:label}}")
    public CommonResult<JSONObject> getJsJson(){
        return success(i18nDataService.getJsJson());
    }
}
