package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("base_system")
public class SystemEntity extends SuperEntity<String> {

    /**
     * 系统名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 系统编号
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 是否是主系统（0-不是，1-是）
     */
    @TableField("F_ISMAIN")
    private Integer isMain;

    /**
     * 系统图标
     */
    @TableField("F_ICON")
    private String icon;

    /**
     * 系统图标
     */
    @TableField("F_PROPERTYJSON")
    private String propertyJson;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark=0;

}
