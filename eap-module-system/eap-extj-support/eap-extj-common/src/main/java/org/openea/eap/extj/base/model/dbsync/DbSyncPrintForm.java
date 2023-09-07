package org.openea.eap.extj.base.model.dbsync;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class DbSyncPrintForm {

    @NotBlank
    @Schema(description = "被同步库连接")
    private String dbLinkFrom;
    @Schema(description = "同步至库类型")
    private String dbTypeTo;
    @Schema(description = "批量同步表名集合")
    private List<String> dbTableList;
    @Schema(description = "转换规则")
    private Map<String, String> convertRuleMap;
    @Schema(description = "输出路径")
    private String outPath;
    @Schema(description = "输出路径")
    private String outFileName;
    @Schema(description = "打印类型")
    private String printType;
    @Schema(description = "多表开关")
    private Boolean multiTabFlag;


}