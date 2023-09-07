package org.openea.eap.extj.base.model.dataInterface;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据接口调用返回模型
 *
 */
@Data
public class DataInterfaceActionVo implements Serializable {

    private Object data;

}
