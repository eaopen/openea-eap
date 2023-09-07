package org.openea.eap.extj.permission.model.user.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Page;

import java.io.Serializable;

/**
 * 通过组织id或关键字查询
 *
 *
 */
@Data
public class PageUser extends Page implements Serializable {
    @Schema(description = "组织id")
    private String organizeId;
}
