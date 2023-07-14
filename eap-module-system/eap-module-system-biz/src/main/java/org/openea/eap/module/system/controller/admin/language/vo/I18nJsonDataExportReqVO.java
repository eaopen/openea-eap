package org.openea.eap.module.system.controller.admin.language.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static org.openea.eap.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 翻译 Excel 导出 Request VO，参数和 I18nJsonDataPageReqVO 是一致的")
@Data
public class I18nJsonDataExportReqVO {

    @Schema(description = "模块，可选")
    private String module;

    @Schema(description = "key/别名")
    private String alias;

    @Schema(description = "名称", example = "张三")
    private String name;

    @Schema(description = "多语言设置json")
    private String json;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
