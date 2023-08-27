package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperExtendEntity;
import lombok.Data;

/**
 * 分组管理
 */
@Data
@TableName("base_group")
public class GroupEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

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
     * 类型
     */
    @TableField("F_TYPE")
    private String type;

}
