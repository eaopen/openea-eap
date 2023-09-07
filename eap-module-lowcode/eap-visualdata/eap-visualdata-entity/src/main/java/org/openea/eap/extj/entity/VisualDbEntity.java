package org.openea.eap.extj.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 大屏数据源配置
 *
 *
 */
@Data
@TableName("blade_visual_db")
public class VisualDbEntity {
    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 驱动类
     */
    @TableField("DRIVER_CLASS")
    private String driverClass;

    /**
     * 连接地址
     */
    @TableField("URL")
    private String url;

    /**
     * 用户名
     */
    @TableField("USERNAME")
    private String username;

    /**
     * 密码
     */
    @TableField("PASSWORD")
    private String password;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 创建人
     */
    @TableField("CREATE_USER")
    private String createUser;

    /**
     * 创建部门
     */
    @TableField("CREATE_DEPT")
    private String createDept;

    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField("UPDATE_USER")
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;

    /**
     * 是否已删除
     */
    @TableField("IS_DELETED")
    private String isDeleted;

    /**
     * 租户id
     */
    @TableField("F_TENANTID")
    private String tenantId;

}
