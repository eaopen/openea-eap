package org.openea.eap.module.system.controller.admin.language;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - i18n国际化数据")
@RestController
@RequestMapping("/system/i18n-data")
@Validated
public class I18nDataController {

}
