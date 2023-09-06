package org.openea.eap.extj.base.vo;

import lombok.Data;

/**
 * 数据接口弹窗选择
 *
 * 
 */
@Data
public class DataInterfacePageListVO<T> extends PageListVO {
    private String dataProcessing;
}
