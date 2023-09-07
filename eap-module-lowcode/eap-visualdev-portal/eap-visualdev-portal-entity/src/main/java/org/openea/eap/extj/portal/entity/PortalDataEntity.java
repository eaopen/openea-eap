package org.openea.eap.extj.portal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.base.entity.SuperEntity;
import lombok.Data;

import java.io.Serializable;


/**
 * <p>
 * 
 * </p>
 *
 */
@Data
@TableName("base_portal_data")
@Schema(description = "PortalData对象")
public class PortalDataEntity extends SuperEntity<String> implements Serializable {

    @Schema(description = "门户ID")
    @TableField("F_PortalId")
    private String portalId;

    @Schema(description = "PC:网页端 APP:手机端 ")
    @TableField("F_Platform")
    private String platform;

    @Schema(description = "系统ID")
    @TableField("F_System_Id")
    private String systemId;

    @Schema(description = "表单配置JSON")
    @TableField("F_FormData")
    private String formData;

    @Schema(description = "类型（mod：模型、custom：自定义）")
    @TableField("F_Type")
    private String type;

}
