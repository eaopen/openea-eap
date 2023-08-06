package org.openea.eap.extj.message.model.message;

import lombok.Data;

/**
 * 企业微信获取部门的对象模型
 */
@Data
public class QyWebChatDeptModel {
    /**
     * 部门ID
     */
    private Integer id;
    /**
     * 部门中文名称
     */
    private String name;
    /**
     * 部门英文名称
     */
    private String name_en;
    /**
     * 部门的上级部门
     */
    private Integer parentid;
    /**
     * 部门排序
     */
    private Integer order;
}
