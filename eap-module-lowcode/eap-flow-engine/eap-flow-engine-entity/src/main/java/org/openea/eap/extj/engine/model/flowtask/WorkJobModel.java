package org.openea.eap.extj.engine.model.flowtask;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.model.flowmessage.FlowMsgModel;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * 流程监控器参数模型
 *
 *
 */

@Data
@AllArgsConstructor
public class WorkJobModel {
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务对象
     */
    private FlowMsgModel flowMsgModel;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

}
