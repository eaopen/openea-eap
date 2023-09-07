package org.openea.eap.extj.permission.model.user.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类功能
 *
 * 
 */
@Data
public class UserSettingForm {

    @Schema(description = "主要类型")
    private String majorType;
    @Schema(description = "主要Id")
    private String majorId;

    @Schema(description = "菜单类型")
    private Integer menuType;

}
