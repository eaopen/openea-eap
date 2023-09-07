package org.openea.eap.extj.permission.model.organizeadministrator;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 组织关系表模型
 *
 * 
 */
@Data
public class OrganizeAdministratorListVo implements Serializable {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "手机号")
    private String mobilePhone;
    @Schema(description = "组织id")
    private String organizeId;
    @Schema(description = "创建时间")
    private Long creatorTime;

}
