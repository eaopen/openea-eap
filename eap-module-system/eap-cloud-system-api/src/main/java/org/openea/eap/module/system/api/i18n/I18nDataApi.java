package org.openea.eap.module.system.api.i18n;

/**
 * I18n/语言/翻译 API
 * 
 */

import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name =  "RPC 服务 - i18n/语言/翻译")
public interface LanguageDataApi {

    String PREFIX = ApiConstants.PREFIX + "/i18n-data";
}
