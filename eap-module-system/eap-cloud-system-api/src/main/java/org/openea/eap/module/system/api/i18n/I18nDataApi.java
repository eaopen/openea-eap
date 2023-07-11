package org.openea.eap.module.system.api.i18n;

/**
 * I18n/语言/翻译 API
 * 
 */
@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name =  "RPC 服务 - i18n/语言/翻译")
public interface LanguageDtaApi {

    String PREFIX = ApiConstants.PREFIX + "/i18n-data";
}
