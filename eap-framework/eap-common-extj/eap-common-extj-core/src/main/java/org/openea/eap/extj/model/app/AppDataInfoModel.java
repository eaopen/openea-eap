package org.openea.eap.extj.model.app;

import lombok.Data;

import java.util.Date;

@Data
public class AppDataInfoModel {
    /**
     * 单据主键
     */
    private String id;

    /**
     * 对象类型
     */
    private String objectType;

    /**
     * 对象主键
     */
    private String objectId;

    /**
     * 数据
     */
    private String objectData;

    /**
     * 描述
     */
    private String description;

    /**
     * 有效标志
     */
    private Integer enabledMark;

    /**
     * 创建时间
     */
    private Date creatorTime;

    /**
     * 创建用户
     */
    private String creatorUserId;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 删除用户
     */
    private String deleteUserId;

    /**
     * 删除标志
     */
    private Integer deleteMark;

    /**
     * 关联系统id
     */
    private String systemId;
}
