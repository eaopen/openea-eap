package org.openea.eap.extj.model.visualJson;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@Data
public class ColumnDataModel {
    private String searchList;
    private Boolean hasSuperQuery = false;

    /**
     * 合计配置
     */
    private boolean showSummary;
    /**
     * 合计字段
     */
    private List<String> summaryField = new ArrayList<>();
    /**
     * 子表展示样式
     */
    private Integer childTableStyle =1;
    private String columnOptions;
    private String columnList;
    private String defaultColumnList;
    private String sortList;
    private Integer type;
    private String defaultSidx;
    private String sort;
    private Boolean hasPage;
    private Integer pageSize;
    private String treeTitle;
    private String treeDataSource;
    private String treeDictionary;
    private String treeRelation;
    private String treePropsUrl;
    private String treePropsValue;
    private String treePropsChildren;
    private String treePropsLabel;
    private String isLeaf;
    private String groupField;
    private String btnsList;
    private String columnBtnsList;
    private String uploaderTemplateJson;
    /**
     * 自定义按钮区
     */
    private String customBtnsList;
    /**
     * 列表权限
     */
    private Boolean useColumnPermission;
    /**
     * 表单权限
     */
    private Boolean useFormPermission;
    /**
     * 按钮权限
     */
    private Boolean useBtnPermission;
    /**
     * 数据权限
     */
    private Boolean useDataPermission;

    //---以下树形列表属性，type=5的情况
    /**
     * 同步异步（0：同步，1：异步）
     */
    private Integer treeLazyType=0;
    /**
     * 父级字段
     */
    private String parentField;
    /**
     * 子级字段
     */
    private String subField;
    //---以上树形列表属性，type=5的情况
    /**
     * 左侧树同步异步
     */
    private Integer treeSynType;
    /**
     * 左侧树查询
     */
    private Boolean hasTreeQuery;

    private String treeInterfaceId;
    private String treeTemplateJson;

    private List<Map> ruleList;
}
