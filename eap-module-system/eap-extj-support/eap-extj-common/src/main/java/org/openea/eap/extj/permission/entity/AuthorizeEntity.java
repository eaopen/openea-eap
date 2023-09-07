package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperBaseEntity;

/**
 * 操作权限
 */
@Data
@TableName("base_authorize")
public class AuthorizeEntity extends SuperBaseEntity.SuperCBaseEntity<String> {

    /**
     * 项目类型
     */
    @TableField("F_ITEMTYPE")
    private String itemType;

    /**
     * 项目主键
     */
    @TableField("F_ITEMID")
    private String itemId;

    /**
     * 对象类型
     */
    @TableField("F_OBJECTTYPE")
    private String objectType;

    /**
     * 对象主键
     */
    @TableField("F_OBJECTID")
    private String objectId;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

}