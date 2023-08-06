package org.openea.eap.extj.base.model.filter;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class RuleInfo {
    /**
     * 字段注释
     */
    private String fieldName;
    /**
     * 运算符
     */
    @Alias("symbol")
    private  String operator;
    /**
     * 逻辑拼接符号
     */
    private String logic;
    /**
     * 组件标识
     */
    private String jnpfKey;
    /**
     * 字段key
     */
    private String field;
    /**
     * 自定义的值
     */
    private String fieldValue;

    private String fieldValue2;

    /**
     * 显示类型
     */
    private String showLevel;


    /**
     * 日期格式
     */
    private String format;

    /**
     * 数字精度
     */
    private String precision;

}
