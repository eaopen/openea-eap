package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("base_sysconfig")
public class SysConfigEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 名称
     */
    @TableField("F_NAME")
    private String name;

    /**
     * 键
     */
    @TableField("F_KEY")
    private String fkey;

    /**
     * 值
     */
    @TableField("F_VALUE")
    private String value;

    /**
     * 分类
     */
    @TableField("F_CATEGORY")
    private String category;

}

