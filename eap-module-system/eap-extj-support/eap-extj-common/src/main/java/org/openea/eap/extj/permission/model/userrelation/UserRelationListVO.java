package org.openea.eap.extj.permission.model.userrelation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * 
 */
@Data
public class UserRelationListVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "成员id")
    private String userId;
    @Schema(description = "用户id")
    private String account;
    @Schema(description = "用户真实姓名")
    private String realName;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "所属公司")
    private String organize;
    @Schema(description = "所属部门")
    private String department;
    @Schema(description = "添加时间(时间戳)")
    private Long creatorTime;
}
