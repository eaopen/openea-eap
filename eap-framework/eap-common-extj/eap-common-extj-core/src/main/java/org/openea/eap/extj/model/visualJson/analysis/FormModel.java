package org.openea.eap.extj.model.visualJson.analysis;

import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.fields.slot.SlotModel;
import lombok.Data;

import java.util.List;

/**
 * 解析引擎
 *
 *
 */
@Data
public class FormModel {

    /**
     * 卡片
     */
    private String shadow;
    private String header;

    /**
     * 栅格
     */
    private Integer span;

    /**
     * 标签页
     */
    private String title;
    private String name;
    private String model;
    private Boolean accordion;

    /**
     * 标签页
     */
    private String tabPosition;
    private String type;

    /**
     * 折叠、标签公用
     */
    private String active;

    /**判断折叠、标签是否最外层 0.不是 1.是**/
    private String outermost;

    /**
     * 折叠、标签公用的子节点
     */
    private List<FieLdsModel> children;

    /**
     * 分组标题
     */
    private String content;
    /**
     * 分割线
     */
    private SlotModel slot;
    /**
     * 文本
     */
    private String textStyle;
    private String style;
    private ConfigModel config;

    /**
     * 分组标签、分割线公用
     */
    private String contentposition;

    /**
     *按钮
     */
    private String align;
    private String buttonText;

    /**
     * app代码生成器
     */
    private int childNum;

    /**
     * 二维码条形码
     */
    private String dataType ="";

    private String relationField;

    private String visibility;

    private String href;

    private String target;

    private boolean closable;

    private boolean showIcon;

    private String selectType;

    private boolean app = true;
    private boolean pc = true;

    //自定义
    private String ableDepIds;
    private String ablePosIds;
    private String ableUserIds;
    private String ableRoleIds;
    private String ableGroupIds;
}
