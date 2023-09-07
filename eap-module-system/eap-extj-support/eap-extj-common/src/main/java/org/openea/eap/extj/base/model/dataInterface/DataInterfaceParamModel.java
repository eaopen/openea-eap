package org.openea.eap.extj.base.model.dataInterface;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 */
@Data
public class DataInterfaceParamModel implements Serializable {

    private String tenantId;

    private String origin;

    private List<DataInterfaceModel> paramList;

}
