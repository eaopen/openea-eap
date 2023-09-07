package org.openea.eap.extj.base.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;

import java.time.LocalDateTime;

/**
 * 打印模板数树形视图对象
 *
 * 
 */
@Data
public class PrintDevTreeModel extends SumTree {

    /**
     * 分类下模板数量
     */
    private Integer num;

    /**
     * 主键_id
     */
    private String id;

    /**
     * 名称
     */
    private String fullName;

    /**
     * 编码
     */
    private String enCode;

    /**
     * 分类
     */
    private String category;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序码
     */
    private Integer sortCode;

    /**
     * 有效标志
     */
    private Integer enabledMark;

    /**
     * 创建时间
     */
    private Long creatorTime;

    /**
     * 创建用户_id
     */
    @JSONField(name = "creatorUserId")
    private String creatorUser;

    /**
     * 修改时间
     */
    private Long lastModifyTime;

    /**
     * 修改用户_id
     */
    @JSONField(name = "lastModifyUserId")
    private String lastModifyUser;

    /**
     * 删除标志
     */
    private Integer deleteMark;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 删除用户_id
     */
    private String deleteUserId;

    /**
     * 连接数据 _id
     */
    private String dbLinkId;

    /**
     * sql语句
     */
    private String sqlTemplate;

    /**
     * 左侧字段
     */
    private String leftFields;

    /**
     * 打印模板
     */
    private String printTemplate;


}
