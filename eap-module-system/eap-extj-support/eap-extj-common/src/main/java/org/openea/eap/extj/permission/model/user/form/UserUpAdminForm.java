package org.openea.eap.extj.permission.model.user.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 超级管理员设置表单参数
 *
 * 
 */
@Data
public class UserUpAdminForm {

    @Schema(description = "超级管理id集合")
    List<String> adminIds;

}
