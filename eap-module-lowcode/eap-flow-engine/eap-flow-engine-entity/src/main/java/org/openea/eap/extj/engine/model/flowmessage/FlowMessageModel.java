package org.openea.eap.extj.engine.model.flowmessage;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorRecordEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.MsgConfig;
import org.openea.eap.extj.engine.enums.FlowMessageEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowMessageModel {
    private String title = "";
    private Integer type =  FlowMessageEnum.wait.getCode();
    private Integer status;
    private MsgConfig msgConfig = new MsgConfig();
    private List<String> userList = new ArrayList<>();
    private Map<String, Object> data = new HashMap<>();
    private Map<String,String> contMsg = new HashMap<>();
    private String  fullName;
    private FlowTaskOperatorRecordEntity recordEntity;
    private UserInfo userInfo;
}
