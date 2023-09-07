package org.openea.eap.extj.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperBaseEntity;

@Data
@TableName("base_userrelation")
public class UserRelationEntity extends SuperBaseEntity.SuperCBaseEntity<String> {

    /**
     * 用户主键
     */
    @TableField("F_USERID")
    private String userId;

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