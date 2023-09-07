package org.openea.eap.extj.model.visualdb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class VisualDbInfoVO {
    /** 主键 */
    @Schema(description = "主键")
    private String id;

    /** 名称 */
    @Schema(description = "名称")
    private String name;

    /** 驱动类 */
    @Schema(description = "驱动类")
    private String driverClass;

    /** 连接地址 */
    @Schema(description = "连接地址")
    private String url;

    /** 用户名 */
    @Schema(description = "用户名")
    private String username;

    /** 密码 */
    @Schema(description = "密码")
    private String password;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 状态 */
    @Schema(description = "状态")
    private String status;

    /** 是否已删除 */
    @Schema(description = "是否已删除")
    private String isDeleted;
}
