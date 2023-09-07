package org.openea.eap.extj.base.model.dbsync;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
public class DbSyncForm {
    @NotBlank(message = "必填")
    @Schema(description = "被同步库连接")
    private String dbConnectionFrom;
    @NotBlank(message = "必填")
    @Schema(description = "同步至库连接")
    private String dbConnectionTo;
    @NotBlank(message = "必填")
    @Schema(description = "单个同步表名")
    private String dbTable;
    @Schema(description = "批量同步表名集合")
    private List<String> dbTableList;
    @Schema(description = "被转换库名")
    private String dbNameFrom;
    @Schema(description = "转换库名")
    private String dbNameTo;
    @Schema(description = "转换规则")
    private Map<String, String> convertRuleMap;

}