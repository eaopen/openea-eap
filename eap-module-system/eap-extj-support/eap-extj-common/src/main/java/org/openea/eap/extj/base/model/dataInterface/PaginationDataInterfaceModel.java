package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 工作流选择时使用
 *
 */
@Data
public class PaginationDataInterfaceModel extends PaginationDataInterface implements Serializable {
    /**
     * 请求方式
     */
    @Schema(description = "请求方式")
    private String dataType;
}
