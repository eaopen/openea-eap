package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

/**
 * 类功能
 *
 */
@Data
public class PermissionEntityBase extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTYJSON")
    private String propertyJson;

}

