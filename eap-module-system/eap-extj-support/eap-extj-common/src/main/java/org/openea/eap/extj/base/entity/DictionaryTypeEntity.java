package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 字典分类
 */
@Data
@TableName("base_dictionarytype")
public class DictionaryTypeEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 上级
     */
    @TableField("F_PARENTID")
    private String parentId;

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
     * 树形
     */
    @TableField("F_ISTREE")
    private Integer isTree;

}
