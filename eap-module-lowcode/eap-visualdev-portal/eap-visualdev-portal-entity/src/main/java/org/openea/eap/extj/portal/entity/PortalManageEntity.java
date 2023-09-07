package org.openea.eap.extj.portal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 门户管理
 * </p>
 */
@TableName("base_portal_manage")
@Schema(description = "PortalManage对象")
@Data
public class PortalManageEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    @Schema(description = "门户_id")
    @TableField("F_Portal_Id")
    private String portalId;

    @Schema(description = "系统_id")
    @TableField("F_System_Id")
    private String systemId;

    @Schema(description = "默认首页")
    @TableField("F_Home_Page_Mark")
    private Integer homePageMark;

    @Schema(description = "平台")
    @NotNull(message = "必填")
    @TableField("F_Platform")
    private String platform;

}
