package org.openea.eap.extj.model.visualJson.config;


import org.openea.eap.extj.model.visualJson.FieLdsModel;
import lombok.Data;
import org.openea.eap.extj.model.visualJson.TemplateJsonModel;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConfigModel {
    private String label;
    private String labelWidth;
    private Boolean showLabel;
    private Boolean changeTag;
    private Boolean border;
    private String tag;
    private String tagIcon;
    //是否必填
    private boolean required = false;
    //是否唯一
    private Boolean unique = false;
    private String layout;
    private String dataType;
    private Integer span = 24;
    private String jnpfKey;
    private String dictionaryType;
    private Integer formId;
    private String relationTable;
    private Long renderKey;
    private Integer columnWidth;
    private List<RegListModel> regList;
    private String reg;
    private Object defaultValue;
    private Boolean defaultCurrent;
    private String active;

    /**
     * 提示语
     */
    private String title;
    private String type;
    private Boolean showIcon;
    private Boolean closable;
    /**
     * app静态数据
     */
    private String options;
    /**
     * 判断defaultValue类型
     */
    private String valueType;
    private String propsUrl;
    private String optionType;
    private ConfigPropsModel props;
    /**
     * 子表添加字段
     */
    private Boolean showTitle;
    private String tableName;
    private String aliasClassName;
    private List<FieLdsModel> children;

    /**
     * 多端显示
     */
    private String visibility="[\"app\",\"pc\"]" ;


    private List<TemplateJsonModel> templateJson = new ArrayList();

    /**
     * 单据规则使用
     */
    private String rule;

    /**
     * 验证规则触发方式
     */
    private String trigger="blur";
    /**
     * 隐藏
     */
    private Boolean noShow=false;
    /**
     * app代码生成器
     */
    private int childNum;
    private String model;

    /**
     * 代码生成器多端显示
     */
    private boolean app = true;
    private boolean pc = true;

    /**
     * 高级查询
     */
    private String parentVModel;

    /**
     * 展示 存储数据
     */
    private Integer isStorage;


    private boolean startTimeRule;
    private String startTimeValue;
    private String startTimeType;
    private String startTimeTarget;
    private String startRelationField;

    public boolean getStartTimeRule(){
        return startTimeRule;
    }

    private boolean endTimeRule;
    private String endTimeValue;
    private String endTimeType;
    private String endTimeTarget;
    private String endRelationField;

    public boolean getEndTimeRule(){
        return endTimeRule;
    }

}
