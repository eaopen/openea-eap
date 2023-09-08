package org.openea.eap.extj.base.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 打印模板-数查询对象
 *
 * 
 */
@Data
public class PrintDevDataQuery {

    /**
     * 打印模板id
     */
    @NotBlank(message = "必填")
    @Schema(description = "打印模板id")
    private String id;

    /**
     * 表单id
     */
    @NotBlank(message = "必填")
    @Schema(description = "表单id")
    private String formId;

    @NotBlank(message = "必填")
    @Schema(description = "打印模板id")
    private List<String> ids;

}
