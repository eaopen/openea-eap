package org.openea.eap.extj.engine.model.flowtask;

import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.FlowHandleModel;
import lombok.Data;

@Data
public class FlowRevokeModel {
    private FlowTaskEntity flowTaskEntity;
    private FlowHandleModel flowHandleModel;
}
