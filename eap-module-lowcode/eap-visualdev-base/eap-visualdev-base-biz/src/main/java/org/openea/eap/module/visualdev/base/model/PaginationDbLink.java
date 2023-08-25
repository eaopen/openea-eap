package org.openea.eap.module.visualdev.base.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;

@Data
public class PaginationDbLink extends Pagination {

    @Schema(description = "数据库类型")
    private String dbType;

}