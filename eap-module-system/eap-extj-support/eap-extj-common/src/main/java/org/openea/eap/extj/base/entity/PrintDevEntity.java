package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 打印模板-实体类
 *
 * 
 */
@Data
@EqualsAndHashCode
@TableName("base_printdev")
public class PrintDevEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @TableField("F_FullName")
    private String fullName;

    /**
     * 编码
     */
    @TableField("F_Encode")
    private String enCode;

    /**
     * 分类
     */
    @TableField("F_Category")
    private String category;

    /**
     * 类型
     */
    @TableField("F_Type")
    private Integer type;

    /**
     * 连接数据 _id
     */
    @TableField("F_DbLinkId")
    private String dbLinkId;

    /**
     * sql语句
     */
    @TableField("F_SqlTemplate")
    private String sqlTemplate;

    /**
     * 左侧字段
     */
    @TableField("F_LeftFields")
    private String leftFields;

    /**
     * 打印模板
     */
    @TableField("F_PrintTemplate")
    private String printTemplate;

    /**
     * 纸张参数
     */
    @TableField("F_PageParam")
    private String pageParam;

}
