package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据接口调用日志
 */
@Data
@TableName("base_datainterfacelog")
public class DataInterfaceLogEntity extends SuperBaseEntity.SuperTBaseEntity<String> implements Serializable {

    /**
     * 调用接口id
     */
    @TableField("F_InvokId")
    private String invokId;

    /**
     * 调用时间
     */
    @TableField(value = "F_InvokTime", fill = FieldFill.INSERT)
    private Date invokTime;

    /**
     * 调用者id
     */
    @TableField("F_UserId")
    private String userId;

    /**
     * 请求ip
     */
    @TableField("F_InvokIp")
    private String invokIp;

    /**
     * 请求设备
     */
    @TableField("F_InvokDevice")
    private String invokDevice;

    /**
     * 请求类型
     */
    @TableField("F_InvokType")
    private String invokType;

    /**
     * 请求耗时
     */
    @TableField("F_InvokWasteTime")
    private Integer invokWasteTime;

    /**
     * 接口授权AppId
     */
    @TableField("F_OAUTHAPPID")
    private String oauthAppId;

}
