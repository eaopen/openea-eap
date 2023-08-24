package org.openea.eap.module.visualdev.base.model.dataInterface;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DataInterfaceMarkModel implements Serializable {

    /**
     * 标记名称
     */
    private String markName;

    /**
     * 值
     */
    private Object value;
}
