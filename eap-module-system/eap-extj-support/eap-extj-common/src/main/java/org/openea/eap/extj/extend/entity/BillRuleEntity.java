package org.openea.eap.extj.extend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

/**
 * 单据规则
 *
 * 
 */
@Data
@TableName("base_billrule")
public class BillRuleEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 单据名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 单据编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 单据前缀
     */
    @TableField("F_PREFIX")
    private String prefix;

    /**
     * 日期格式
     */
    @TableField("F_DATEFORMAT")
    private String dateFormat;

    /**
     * 流水位数
     */
    @TableField("F_DIGIT")
    private Integer digit;

    /**
     * 流水起始
     */
    @TableField("F_STARTNUMBER")
    private String startNumber;

    /**
     * 流水范例
     */
    @TableField("F_EXAMPLE")
    private String example;

    /**
     * 当前流水号
     */
    @TableField("F_THISNUMBER")
    private Integer thisNumber;

    /**
     * 输出流水号
     */
    @TableField("F_OUTPUTNUMBER")
    private String outputNumber;

    /**
     * 分类
     */
    @TableField("F_Category")
    private  String category;

}
