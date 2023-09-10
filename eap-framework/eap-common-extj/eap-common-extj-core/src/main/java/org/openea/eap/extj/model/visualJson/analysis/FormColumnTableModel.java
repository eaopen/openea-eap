package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;

import java.util.List;

/**
 * 解析引擎
 *
 * 
 */
@Data
public class FormColumnTableModel {

    /**json原始名称**/
    private String tableModel;
    /**表名称**/
    private String tableName;
    /**标题**/
    private String label;
    /**宽度**/
    private Integer span;
    /**是否显示标题**/
    private boolean showTitle;
    /**按钮名称**/
    private String actionText;
    /**子表的属性**/
    private List<FormColumnModel> childList;
    /**app子表属性**/
    private String fieLdsModel;

    /**
     * 子表是否合计
     */
    private Boolean showSummary;

    /**
     * 子表合计字段
     */
    private String summaryField;

    /**
     * app子表合计名称
     */
    private String summaryFieldName;

    /**
     * 子表表单
     */
    private Integer addType = 0;
    private String addTableConf;

    /**
     * 代码生成器多端显示
     */
    private boolean app = true;
    private boolean pc = true;

    private String visibility;
    private boolean required = false;

    private String aliasClassName;


    private List<String> thousandsField;

}
