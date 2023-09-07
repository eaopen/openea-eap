package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("base_dictionarydata")
public class DictionaryDataEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

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
     * 拼音
     */
    @TableField("F_SIMPLESPELLING")
    private String simpleSpelling;

    /**
     * 默认
     */
    @TableField("F_ISDEFAULT")
    private Integer isDefault;

    /**
     * 类别主键
     */
    @TableField("F_DICTIONARYTYPEID")
    private String dictionaryTypeId;

}
