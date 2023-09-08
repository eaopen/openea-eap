package org.openea.eap.extj.base.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 打印模板-数查询对象
 *
 * 
 */
@Data
public class PrintDevFieldsQuery {

    /**
     * sql语句
     */
    @NotBlank(message = "必填")
    @Schema(description = "sql语句")
    private String sqlTemplate;

    /**
     * 连接id
     */
    @NotBlank(message = "必填")
    @Schema(description = "连接id")
    private String dbLinkId;

}
