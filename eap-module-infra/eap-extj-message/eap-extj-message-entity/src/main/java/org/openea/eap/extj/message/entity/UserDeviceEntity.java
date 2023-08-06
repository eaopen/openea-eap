package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperEntity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * 短信变量表
 */
@Data
@TableName("base_user_device")
public class UserDeviceEntity extends SuperEntity<String> {

    /** 用户id **/
    @TableField("F_USERID")
    private String userId;

    /** 设备id **/
    @TableField("F_CLIENTID")
    private String clientId;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;
}
