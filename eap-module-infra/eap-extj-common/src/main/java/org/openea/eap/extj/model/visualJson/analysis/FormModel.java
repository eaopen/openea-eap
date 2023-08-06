package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.SlotModel;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;

import java.util.List;

@Data
public class FormModel {

    private String shadow;
    private String header;
    private Integer span;
    private String title;
    private String name;
    private String model;
    private Boolean accordion;
    private String tabPosition;
    private String type;
    private String active;
    private String outermost;
    private List<FieLdsModel> children;
    private String content;
    private SlotModel slot;
    private String textStyle;
    private String style;
    private ConfigModel config;
    private String contentposition;
    private String align;
    private String buttonText;
    private int childNum;
    private String dataType = "";
    private String relationField;
    private String visibility;
    private String href;
    private String target;
    private String tipLabel;
    private boolean closable;
    private boolean showIcon;
    private String selectType;
    private boolean merged;
    private String colspan;
    private String rowspan;
    private String rowType;
    private String description;
    private String closeText;
    private String borderType;
    private String borderColor;
    private String borderWidth;
    private String ableDepIds;
    private String ablePosIds;
    private String ableUserIds;
    private String ableRoleIds;
    private String ableGroupIds;

}
