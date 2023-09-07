package org.openea.eap.extj.portal.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 门户
 */
@Data
@TableName("base_portal")
public class PortalEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    @Schema(description = "名称")
    @TableField("F_FULLNAME")
    private String fullName;

    @Schema(description = "编码")
    @TableField("F_ENCODE")
    private String enCode;

    @Schema(description = "分类(数据字典维护)")
    @TableField("F_CATEGORY")
    private String category;

    @Schema(description = "类型(0-页面设计,1-自定义路径)")
    @TableField("F_TYPE")
    private Integer type;

    @Schema(description = "静态页面路径")
    @TableField("F_CUSTOMURL")
    private String customUrl;

    @Schema(description = "类型(0-页面,1-外链)")
    @TableField("F_LINKTYPE")
    private Integer linkType;

    @Schema(description = "移动锁定(0-未锁定,1-锁定)")
    @TableField(value = "F_ENABLEDLOCK", updateStrategy = FieldStrategy.IGNORED)
    private Integer enabledLock;

}
