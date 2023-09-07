package org.openea.eap.extj.generater.model;


import org.openea.eap.extj.model.visualJson.TableModel;
import lombok.Data;

import java.util.List;


@Data
public class Template7Model {
    /**
     * 版本
     */
    private String version = "V3.2.0";
    /**
     * 版权
     */
    private String copyright;
    /**
     * 创建人员
     */
    private String createUser;
    /**
     * 创建日期
     */
    private String createDate;
    /**
     * 功能描述
     */
    private String description;
    /**
     * 子类功能名称
     */
    private String subClassName;
    /**
     * 主类功能名称
     */
    private String className;

    /**
     * 表
     */
    private String tableName;

    /**
     * 表单页名
     */
    private String formPageName;
    /**
     * 列表页名
     */
    private String indexPageName;
    /**
     * 后端目录
     */
    private String serviceDirectory;
    /**
     * 前端目录
     */
    private String webDirectory;
    /**
     * 表单标题
     */
    private String formTitle;
    /**
     * 弹窗类型
     */
    private String formDialog;
    /**
     * 表单宽度
     */
    private int formWidth;
    /**
     * 表单高度
     */
    private int formHeight;
    /**
     * 表单Tabs
     */
    private String[] formTabs;
    /**
     * 列表左边树 - 是否显示
     */
    private int treeIsShow;
    /**
     * 列表左边树 - 树形标题
     */
    private String treeTitle;
    /**
     * 列表左边树 - 数据来源
     */
    private String treeDataSource;
    /**
     * 列表左边树 - 数据字典
     */
    private String treeDictionary;
    /**
     * 列表左边树 - 数据选择
     */
    private String treePropsUrl;
    /**
     * 列表左边树 - 主键字段
     */
    private String treePropsValue;
    /**
     * 列表左边树 - 父级字段
     */
    private String treePropsChildren;
    /**
     * 列表左边树 - 显示字段
     */
    private String treePropsLabel;
    /**
     * 列表左边树 - 关联字段
     */
    private String treeRelation;
    /**
     * 按钮 - 新建
     */
    private String indexBtnAddName;
    /**
     * 按钮 - 编辑
     */
    private String indexBtnEditName;
    /**
     * 按钮 - 删除
     */
    private String indexBtnRemoveName;
    /**
     * 列表 - 标题
     */
    private String indexListTitle;
    /**
     * 列表 - 分页
     */
    private int indexGridIsPage;

    /**
     *  数据关联 - 集合
     */
    private List<TableModel> dbTableRelation;
}
