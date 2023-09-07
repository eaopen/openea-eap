package org.openea.eap.extj.permission.model.user.mod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Page;

/**
 * 获取岗位成员
 *
 * 
 */
@Data
public class UsersByPositionModel extends Page {
    @Schema(description = "岗位id")
    private String positionId;
}
