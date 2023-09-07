package org.openea.eap.extj.engine.util;

import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ChildNode;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ProperCond;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.Custom;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.DateProperties;
import org.openea.eap.extj.engine.model.flowtask.FlowConditionModel;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 在线工作流开发
 *
 * 
 */
@Data
public class FlowJsonUtil {

    /**
     * 外层节点
     **/
    private static String cusNum = "0";

    /**
     * 获取下一节点
     **/
    public static String getNextNode(FlowConditionModel conditionModel) {
        String next = nextNodeId(conditionModel);
        return next;
    }

    /**
     * 下一节点id
     **/
    private static String nextNodeId(FlowConditionModel conditionModel) {
        List<ChildNodeList> childNodeListAll = conditionModel.getChildNodeListAll();
        String nodeId = conditionModel.getNodeId();
        String nextId = "";
        boolean flag = false;
        ChildNodeList childNode = childNodeListAll.stream().filter(t -> t.getCustom().getNodeId().equals(nodeId)).findFirst().orElse(null);
        String contextType = childNode.getConditionType();
        //条件、分流的判断
        if (StringUtils.isNotEmpty(contextType)) {
            if (FlowCondition.CONDITION.equals(contextType)) {
                List<String> nextNodeId = new ArrayList<>();
                getContionNextNode(conditionModel, nextNodeId);
                nextId = String.join(",", nextNodeId);
                if (StringUtils.isNotEmpty(nextId)) {
                    flag = true;
                }
            } else {
                nextId = childNode.getCustom().getFlowId();
                flag = true;
            }
        }
        //子节点
        if (!flag) {
            if (childNode.getCustom().getFlow()) {
                nextId = childNode.getCustom().getFlowId();
            } else {
                //不是外层的下一节点
                if (!cusNum.equals(childNode.getCustom().getNum())) {
                    nextId = childNode.getCustom().getFirstId();
                    if (childNode.getCustom().getChild()) {
                        nextId = childNode.getCustom().getChildNode();
                    }
                } else {
                    //外层的子节点
                    if (childNode.getCustom().getChild()) {
                        nextId = childNode.getCustom().getChildNode();
                    }
                }
            }
        }
        return nextId;
    }

    //---------------------------------------------------递归获取当前的上节点和下节点----------------------------------------------

    /**
     * 获取当前已完成节点
     **/
    private static void upList(List<FlowTaskNodeEntity> flowTaskNodeList, String node, Set<String> upList, String[] tepId) {
        FlowTaskNodeEntity entity = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(node)).findFirst().orElse(null);
        if (entity != null) {
            List<String> list = flowTaskNodeList.stream().filter(t -> t.getSortCode() != null && t.getSortCode() < entity.getSortCode()).map(t -> t.getNodeCode()).collect(Collectors.toList());
            list.removeAll(Arrays.asList(tepId));
            upList.addAll(list);
        }
    }

    /**
     * 获取当前未完成节点
     **/
    private static void nextList(List<FlowTaskNodeEntity> flowTaskNodeList, String node, Set<String> nextList, String[] tepId) {
        FlowTaskNodeEntity entity = flowTaskNodeList.stream().filter(t -> t.getNodeCode().equals(node)).findFirst().orElse(null);
        if (entity != null) {
            List<String> list = flowTaskNodeList.stream().filter(t -> t.getSortCode() != null && t.getSortCode() > entity.getSortCode()).map(t -> t.getNodeCode()).collect(Collectors.toList());
            list.removeAll(Arrays.asList(tepId));
            nextList.addAll(list);
        }
    }

    //---------------------------------------------------条件----------------------------------------------

    /**
     * 递归条件
     **/
    private static void getContionNextNode(FlowConditionModel conditionModel, List<String> nextNodeId) {
        String nodeId = conditionModel.getNodeId();
        List<ConditionList> conditionListAll = conditionModel.getConditionListAll();
        List<ConditionList> conditionAll = conditionListAll.stream().filter(t -> t.getPrevId().equals(nodeId)).collect(Collectors.toList());
        for (ConditionList condition : conditionAll) {
            List<ProperCond> conditions = condition.getConditions();
            boolean flag = nodeConditionDecide(conditionModel, conditions);
            //判断条件是否成立或者其他情况条件
            if (flag || condition.getIsDefault()) {
                String conditionId = condition.getNodeId();
                List<ConditionList> childCondition = conditionListAll.stream().filter(t -> t.getPrevId().equals(conditionId)).collect(Collectors.toList());
                if (childCondition.size() > 0) {
                    conditionModel.setNodeId(conditionId);
                    getContionNextNode(conditionModel, nextNodeId);
                }
                if (nextNodeId.size() == 0) {
                    //先获取条件下的分流节点
                    if (condition.getFlow()) {
                        nextNodeId.add(condition.getFlowId());
                    } else {
                        //条件的子节点
                        if (condition.getChild()) {
                            nextNodeId.add(condition.getChildNodeId());
                        } else {
                            nextNodeId.add(condition.getFirstId());
                        }
                    }
                }
            }
        }
    }

    /**
     * 节点条件判断
     **/
    private static boolean nodeConditionDecide(FlowConditionModel conditionModel, List<ProperCond> conditionList) {
        String data = conditionModel.getData();
        boolean flag = false;
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("js");
        Map<String, Object> map = JsonUtil.stringToMap(data);
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < conditionList.size(); i++) {
            ProperCond properCond = conditionList.get(i);
            String contain = "!=-1";
            String logic = properCond.getLogic();
            String field = properCond.getField();
            String jnpfKey = properCond.getJnpfKey();
            int fieldType = properCond.getFieldType();
            Object form = fieldType == 1 ? formValue(conditionModel, jnpfKey, map.get(field)) : formula(properCond, map);
            String formValue = "'" + form + "'";
            List<String> numList = new ArrayList(){{add(JnpfKeyConsts.CALCULATE);add(JnpfKeyConsts.NUM_INPUT);}};
            List<String> timeList = new ArrayList(){{add(JnpfKeyConsts.TIME);}};
            if(numList.contains(jnpfKey)){
                formValue = "parseFloat("+form+")";
            }
            if(timeList.contains(jnpfKey)){
                if(ObjectUtil.isNotEmpty(form)) {
                    formValue = String.valueOf(form).replaceAll(":","");
//                    String formatterType = "yyyy-MM-dd ";
//                    String[] timeValue = String.valueOf(form).split(":");
//                    for(int k=0;k<timeValue.length;k++){
//                        if(k==0){
//                            formatterType+="HH";
//                        }else if(k==1){
//                            formatterType+=":mm";
//                        }else if(k==2){
//                            formatterType+=":ss";
//                        }
//                    }
//                    try {
//                        SimpleDateFormat formatter = new SimpleDateFormat(formatterType);
//                        Date date = formatter.parse(String.valueOf(form));
//                        form = date.getTime();
//                    }catch (Exception e){
//
//                    }
//                    formValue = "parseFloat(" + form + ")";
                }
            }
            String symbol = properCond.getSymbol();
            boolean include = ("like".equals(symbol) || "notLike".equals(symbol));
            if ("<>".equals(symbol)) {
                symbol = "!=";
            }
            int fieldValueType = properCond.getFieldValueType();
            String valueJnpfKey = properCond.getFieldValueJnpfKey();
            Object value = fieldValueType == 2 ? filedValue(conditionModel, properCond.getFieldValue(), valueJnpfKey, form) : filedData(conditionModel, properCond.getFieldValue(), valueJnpfKey, form);
            String fieldValue = "'" + value + "'";
            if(numList.contains(valueJnpfKey)){
                fieldValue = "parseFloat("+value+")";
            }
            if(timeList.contains(jnpfKey)){
                if(ObjectUtil.isNotEmpty(value)) {
                    fieldValue = String.valueOf(value).replaceAll(":","");
//                    String formatterType = "yyyy-MM-dd ";
//                    String[] timeValue = String.valueOf(value).split(":");
//                    for(int k=0;k<timeValue.length;k++){
//                        if(k==0){
//                            formatterType+="HH";
//                        }else if(k==1){
//                            formatterType+=":mm";
//                        }else if(k==2){
//                            formatterType+=":ss";
//                        }
//                    }
//                    try {
//                        SimpleDateFormat formatter = new SimpleDateFormat(formatterType);
//                        Date date = formatter.parse(String.valueOf(value));
//                        value = date.getTime();
//                    }catch (Exception e){
//
//                    }
//                    fieldValue = "parseFloat(" + value + ")";
                }
            }
            if (">".equals(symbol) || ">=".equals(symbol) || "<=".equals(symbol) || "<".equals(symbol)) {
                formValue = "parseFloat(" + formValue + ")";
                fieldValue = "parseFloat(" + fieldValue + ")";
            }
            String pression = formValue + symbol + fieldValue;
            if (include) {
                if ("notLike".equals(symbol)) {
                    contain = "==-1";
                }
                symbol = ".indexOf";
                pression = formValue + symbol + "(" + fieldValue + ")" + contain;
            }
            expression.append(pression);
            if (!StringUtils.isEmpty(logic)) {
                if (i != conditionList.size() - 1) {
                    expression.append(" " + logic + " ");
                }
            }
        }
        try {
            flag = (Boolean) scriptEngine.eval(expression.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    /**
     * 条件数据修改
     *
     * @param conditionModel
     * @param value
     */
    private static Object filedValue(FlowConditionModel conditionModel, Object value, String jnpfKey, Object form) {
        UserInfo userInfo = conditionModel.getUserInfo();
        if ("currentUser".equals(value)) {
            value = userInfo.getUserId();
        }
        UserEntity userEntity = conditionModel.getUserEntity();
        FlowTaskEntity flowTaskEntity = conditionModel.getFlowTaskEntity();
        try {
            try {
                value = JsonUtil.getJsonToBean(String.valueOf(value), String[][].class);
            } catch (Exception e) {
            }
            try {
                value = JsonUtil.getJsonToList(String.valueOf(value), String.class);
            } catch (Exception e) {
            }
            if (value instanceof String[][]) {
                List<String> id = new ArrayList<>();
                String[][] dataAll = (String[][]) value;
                for (String[] data : dataAll) {
                    for (String values : data) {
                        id.add(values);
                    }
                }
                value = String.join(",", id);
            } else if (value instanceof List) {
                List data = (List) value;
                if (JnpfKeyConsts.CURRORGANIZE.equals(jnpfKey)) {
                    value = data.stream().filter(t -> t.equals(form)).findFirst().orElse(null);
                } else {
                    value = String.join(",", data);
                }
            } else {
                if (JnpfKeyConsts.CREATETIME.equals(jnpfKey)) {
                    value = flowTaskEntity.getCreatorTime().getTime() + "";
                } else if (JnpfKeyConsts.CREATEUSER.equals(jnpfKey)) {
                    value = flowTaskEntity.getCreatorUserId();
                } else if (JnpfKeyConsts.CURRORGANIZE.equals(jnpfKey)) {
                    value = userEntity.getOrganizeId();
                } else if (JnpfKeyConsts.CURRPOSITION.equals(jnpfKey)) {
                    value = userEntity.getPositionId();
                } else if (JnpfKeyConsts.MODIFYTIME.equals(jnpfKey)) {
                    value = flowTaskEntity.getLastModifyTime().getTime() + "";
                } else if (JnpfKeyConsts.MODIFYUSER.equals(jnpfKey)) {
                    value = flowTaskEntity.getLastModifyUserId();
                }
            }
        } catch (Exception e) {

        }
        return value;
    }

    /**
     * 条件数据修改
     *
     * @param conditionModel
     * @param value
     */
    private static Object filedData(FlowConditionModel conditionModel, Object value, String jnpfKey, Object form) {
        Map<String, Object> map = JsonUtil.stringToMap(conditionModel.getData());
        value = map.get(value);
        UserEntity userEntity = conditionModel.getUserEntity();
        FlowTaskEntity flowTaskEntity = conditionModel.getFlowTaskEntity();
        try {
            try {
                value = JsonUtil.getJsonToBean(String.valueOf(value), String[][].class);
            } catch (Exception e) {
            }
            try {
                value = JsonUtil.getJsonToList(String.valueOf(value), String.class);
            } catch (Exception e) {
            }
            if (value instanceof String[][]) {
                List<String> id = new ArrayList<>();
                String[][] dataAll = (String[][]) value;
                for (String[] data : dataAll) {
                    for (String values : data) {
                        id.add(values);
                    }
                }
                value = String.join(",", id);
            } else if (value instanceof List) {
                List data = (List) value;
                if (JnpfKeyConsts.CURRORGANIZE.equals(jnpfKey)) {
                    value = data.stream().filter(t -> t.equals(form)).findFirst().orElse(null);
                } else {
                    value = String.join(",", data);
                }
            } else {
                if (JnpfKeyConsts.CREATETIME.equals(jnpfKey)) {
                    value = flowTaskEntity.getCreatorTime().getTime() + "";
                } else if (JnpfKeyConsts.CREATEUSER.equals(jnpfKey)) {
                    value = flowTaskEntity.getCreatorUserId();
                } else if (JnpfKeyConsts.CURRORGANIZE.equals(jnpfKey)) {
                    value = userEntity.getOrganizeId();
                } else if (JnpfKeyConsts.CURRPOSITION.equals(jnpfKey)) {
                    value = userEntity.getPositionId();
                } else if (JnpfKeyConsts.MODIFYTIME.equals(jnpfKey)) {
                    value = flowTaskEntity.getLastModifyTime().getTime() + "";
                } else if (JnpfKeyConsts.MODIFYUSER.equals(jnpfKey)) {
                    value = flowTaskEntity.getLastModifyUserId();
                }
            }
        } catch (Exception e) {

        }
        return value;
    }

    /**
     * 表单数据修改
     *
     * @param form
     */
    private static Object formValue(FlowConditionModel conditionModel, String jnpfKey, Object form) {
        Object result = form;
        UserEntity userEntity = conditionModel.getUserEntity();
        FlowTaskEntity flowTaskEntity = conditionModel.getFlowTaskEntity();
        try {
            try {
                form = JsonUtil.getJsonToBean(String.valueOf(form), String[][].class);
            } catch (Exception e) {
            }
            try {
                form = JsonUtil.getJsonToList(String.valueOf(form), String.class);
            } catch (Exception e) {
            }
            if (form instanceof String[][]) {
                List<String> id = new ArrayList<>();
                String[][] dataAll = (String[][]) form;
                for (String[] data : dataAll) {
                    for (String value : data) {
                        id.add(value);
                    }
                }
                result = String.join(",", id);
            } else if (form instanceof List) {
                result = String.join(",", (List) form);
            } else {
                if (JnpfKeyConsts.CREATETIME.equals(jnpfKey)) {
                    result = flowTaskEntity.getCreatorTime().getTime() + "";
                } else if (JnpfKeyConsts.CREATEUSER.equals(jnpfKey)) {
                    result = flowTaskEntity.getCreatorUserId();
                } else if (JnpfKeyConsts.CURRORGANIZE.equals(jnpfKey)) {
                    result = userEntity.getOrganizeId();
                } else if (JnpfKeyConsts.CURRPOSITION.equals(jnpfKey)) {
                    result = userEntity.getPositionId();
                } else if (JnpfKeyConsts.MODIFYTIME.equals(jnpfKey)) {
                    result = flowTaskEntity.getLastModifyTime().getTime() + "";
                } else if (JnpfKeyConsts.MODIFYUSER.equals(jnpfKey)) {
                    result = flowTaskEntity.getLastModifyUserId();
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 表达式
     */
    private static Object formula(ProperCond properCond, Map<String, Object> data) {
        String result = null;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("function getNum(val) {\n" +
                    "  return isNaN(val) ? 0 : Number(val)\n" +
                    "};\n" +
                    "// 求和\n" +
                    "function SUM() {\n" +
                    "  var value = 0\n" +
                    "  for (var i = 0; i < arguments.length; i++) {\n" +
                    "    value += getNum(arguments[i])\n" +
                    "  }\n" +
                    "  return value\n" +
                    "};\n" +
                    "// 求差\n" +
                    "function SUBTRACT(num1, num2) {\n" +
                    "  return getNum(num1) - getNum(num2)\n" +
                    "};\n" +
                    "// 相乘\n" +
                    "function PRODUCT() {\n" +
                    "  var value = 1\n" +
                    "  for (var i = 0; i < arguments.length; i++) {\n" +
                    "    value = value * getNum(arguments[i])\n" +
                    "  }\n" +
                    "  return value\n" +
                    "};\n" +
                    "// 相除\n" +
                    "function DIVIDE(num1, num2) {\n" +
                    "  return getNum(num1) / (getNum(num2) === 0 ? 1 : getNum(num2))\n" +
                    "};\n" +
                    "// 获取参数的数量\n" +
                    "function COUNT() {\n" +
                    "  var value = 0\n" +
                    "  for (var i = 0; i < arguments.length; i++) {\n" +
                    "    value ++\n" +
                    "  }\n" +
                    "  return value\n" +
                    "};\n");
            String field = field(properCond.getField(), data, null);
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("js");
            String eval = builder.toString() + " var result = " + field + ";";
            scriptEngine.eval(eval);
            double d = (double) scriptEngine.get("result");
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setRoundingMode(RoundingMode.UP);
            result = nf.format(d);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * 赋值
     */
    private static Map<String, List<String>> data(Matcher matcher, Map<String, Object> dataAll) {
        Map<String, List<String>> map = new HashMap<>();
        Map<String, String> keyAll = new HashMap<>();
        while (matcher.find()) {
            String group = matcher.group().replaceAll("\\{", "").replaceAll("}", "");
            keyAll.put(group, group);
        }
        for (String id : keyAll.keySet()) {
            List<String> valueData = new ArrayList<>();
            String valueAll[] = id.split("-");
            String key = valueAll[0];
            Object childDataAll = dataAll.get(key) != null ? dataAll.get(key) : "";
            if (valueAll.length > 1) {
                String data = valueAll[1];
                if (childDataAll instanceof List) {
                    List<Map<String, Object>> childData = (List<Map<String, Object>>) childDataAll;
                    for (Map<String, Object> childDatum : childData) {
                        Object childDatas = childDatum.get(data);
                        valueData.add(childDatas + "");
                    }
                }
            } else if (valueAll.length == 1) {
                valueData.add(childDataAll + "");
            }
            map.put(id, valueData);
        }
        return map;
    }


    //---------------------------------------------------------------解析--------------------------------------------------------------------------

    /**
     * 递归外层的节点
     **/
    public static void childListAll(ChildNode childNode, List<ChildNode> chilNodeList) {
        if (childNode != null) {
            chilNodeList.add(childNode);
            boolean haschildNode = childNode.getChildNode() != null;
            if (haschildNode) {
                ChildNode nextNode = childNode.getChildNode();
                childListAll(nextNode, chilNodeList);
            }
        }
    }

    /**
     * 最外层的json
     **/
    public static void getTemplateAll(ChildNode childNode, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll) {
        List<ChildNode> chilNodeList = new ArrayList<>();
        childListAll(childNode, chilNodeList);
        if (childNode != null) {
            String nodeId = childNode.getNodeId();
            String prevId = childNode.getPrevId();
            boolean haschildNode = childNode.getChildNode() != null;
            boolean hasconditionNodes = childNode.getConditionNodes() != null;
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            //属性赋值
            assignment(properties);
            ChildNodeList childNodeList = new ChildNodeList();
            childNodeList.setProperties(properties);
            //定时器
            DateProperties model = JsonUtil.getJsonToBean(properties, DateProperties.class);
            childNodeList.setTimer(model);
            //自定义属性
            Custom customModel = new Custom();
            customModel.setType(childNode.getType());
            customModel.setNum("0");
            customModel.setFirstId("");
            customModel.setChild(haschildNode);
            customModel.setNodeId(nodeId);
            customModel.setPrevId(prevId);
            customModel.setChildNode(haschildNode == true ? childNode.getChildNode().getNodeId() : "");
            //判断子节点数据是否还有分流节点,有的话保存分流节点id
            if (hasconditionNodes) {
                childNodeList.setConditionType(FlowCondition.CONDITION);
                List<ChildNode> conditionNodes = childNode.getConditionNodes().stream().filter(t -> t.getIsInterflow() != null || t.getIsBranchFlow() != null).collect(Collectors.toList());
                boolean isFlow = conditionNodes.size() > 0;
                if (isFlow) {
                    customModel.setFlow(isFlow);
                    boolean branchFlow = conditionNodes.stream().filter(t -> t.getIsBranchFlow() != null).count() > 0;
                    customModel.setBranchFlow(branchFlow);
                    childNodeList.setConditionType(branchFlow ? FlowCondition.ISBRANCHFLOW : FlowCondition.INTERFLOW);
                    List<String> flowIdAll = conditionNodes.stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                    customModel.setFlowId(String.join(",", flowIdAll));
                }
            }
            childNodeList.setCustom(customModel);
            childNodeListAll.add(childNodeList);
            String firstId = "";
            if (haschildNode) {
                firstId = childNode.getChildNode().getNodeId();
            }
            if (hasconditionNodes) {
                conditionList(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
            if (haschildNode) {
                getchildNode(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
        }
    }

    /**
     * 递归子节点的子节点
     **/
    private static void getchildNode(ChildNode parentChildNodeTest, String firstId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll, List<ChildNode> chilNodeList) {
        ChildNode childNode = parentChildNodeTest.getChildNode();
        if (childNode != null) {
            String nodeId = childNode.getNodeId();
            String prevId = childNode.getPrevId();
            boolean haschildNode = childNode.getChildNode() != null;
            boolean hasconditionNodes = childNode.getConditionNodes() != null;
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properModel = childNode.getProperties();
            //属性赋值
            assignment(properModel);
            ChildNodeList childNodeList = new ChildNodeList();
            childNodeList.setProperties(properModel);
            //定时器
            DateProperties model = JsonUtil.getJsonToBean(properModel, DateProperties.class);
            childNodeList.setTimer(model);
            //自定义属性
            Custom customModel = new Custom();
            customModel.setType(childNode.getType());
            boolean isFirst = chilNodeList.stream().filter(t -> t.getNodeId().equals(nodeId)).count() > 0;
            customModel.setNum(isFirst ? "0" : "1");
            customModel.setFirstId(firstId);
            if (isFirst) {
                customModel.setFirstId(haschildNode ? childNode.getChildNode().getNodeId() : "");
            }
            customModel.setChild(haschildNode);
            customModel.setNodeId(nodeId);
            customModel.setPrevId(prevId);
            customModel.setChildNode(haschildNode == true ? childNode.getChildNode().getNodeId() : "");
            //判断子节点数据是否还有分流节点,有的话保存分流节点id
            if (hasconditionNodes) {
                childNodeList.setConditionType(FlowCondition.CONDITION);
                List<ChildNode> conditionNodes = childNode.getConditionNodes().stream().filter(t -> t.getIsInterflow() != null || t.getIsBranchFlow() != null).collect(Collectors.toList());
                boolean isFlow = conditionNodes.size() > 0;
                if (isFlow) {
                    customModel.setFlow(isFlow);
                    boolean branchFlow = conditionNodes.stream().filter(t -> t.getIsBranchFlow() != null).count() > 0;
                    customModel.setBranchFlow(branchFlow);
                    childNodeList.setConditionType(branchFlow ? FlowCondition.ISBRANCHFLOW : FlowCondition.INTERFLOW);
                    List<String> flowIdAll = conditionNodes.stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                    customModel.setFlowId(String.join(",", flowIdAll));
                }
            }
            childNodeList.setCustom(customModel);
            childNodeListAll.add(childNodeList);
            //条件或者分流递归
            if (hasconditionNodes) {
                conditionList(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
            //子节点递归
            if (haschildNode) {
                getchildNode(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
        }
    }

    /**
     * 条件、分流递归
     **/
    private static void conditionList(ChildNode childNode, String firstId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll, List<ChildNode> chilNodeList) {
        List<ChildNode> conditionNodes = childNode.getConditionNodes();
        if (conditionNodes.size() > 0) {
            //判断是条件还是分流
            //判断父节点是否还有子节点,有的话替换子节点数据
            ChildNode childNodeModel = childNode.getChildNode();
            if (childNodeModel != null) {
                firstId = childNodeModel.getNodeId();
            } else {
                ChildNode nodes = chilNodeList.stream().filter(t -> t.getNodeId().equals(childNode.getNodeId())).findFirst().orElse(null);
                if (nodes != null) {
                    if (nodes.getChildNode() != null) {
                        firstId = childNode.getChildNode().getNodeId();
                    } else {
                        firstId = "";
                    }
                }
            }
            for (ChildNode node : conditionNodes) {
                boolean conditionType = (node.getIsInterflow() == null && node.getIsBranchFlow() == null) ? true : false;
                if (conditionType) {
                    getCondition(node, firstId, childNodeListAll, conditionListAll, chilNodeList);
                } else {
                    getConditonFlow(node, firstId, childNodeListAll, conditionListAll, chilNodeList);
                }
            }
        }
    }

    /**
     * 条件递归
     **/
    private static void getCondition(ChildNode childNode, String firstId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll, List<ChildNode> chilNodeList) {
        if (childNode != null) {
            String nodeId = childNode.getNodeId();
            String prevId = childNode.getPrevId();
            boolean haschildNode = childNode.getChildNode() != null;
            boolean hasconditionNodes = childNode.getConditionNodes() != null;
            boolean isDefault = childNode.getProperties().getIsDefault() != null ? childNode.getProperties().getIsDefault() : false;
            ConditionList conditionList = new ConditionList();
            conditionList.setNodeId(nodeId);
            conditionList.setPrevId(prevId);
            conditionList.setChild(haschildNode);
            conditionList.setTitle(childNode.getProperties().getTitle());
            conditionList.setConditions(childNode.getProperties().getConditions());
            conditionList.setChildNodeId(haschildNode == true ? childNode.getChildNode().getNodeId() : "");
            conditionList.setIsDefault(isDefault);
            conditionList.setFirstId(firstId);
            //判断子节点数据是否还有分流节点,有的话保存分流节点id
            if (hasconditionNodes) {
                List<ChildNode> conditionNodes = childNode.getConditionNodes().stream().filter(t -> t.getIsInterflow() != null || t.getIsBranchFlow() != null).collect(Collectors.toList());
                boolean isFlow = conditionNodes.size() > 0;
                if (isFlow) {
                    conditionList.setFlow(isFlow);
                    boolean branchFlow = conditionNodes.stream().filter(t -> t.getIsBranchFlow() != null).count() > 0;
                    conditionList.setBranchFlow(branchFlow);
                    List<String> flowIdAll = conditionNodes.stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                    conditionList.setFlowId(String.join(",", flowIdAll));
                }
            }
            conditionListAll.add(conditionList);
            //递归条件、分流
            if (hasconditionNodes) {
                conditionList(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
            //递归子节点
            if (haschildNode) {
                getchildNode(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
        }
    }

    /**
     * 条件递归
     **/
    private static void getConditonFlow(ChildNode childNode, String firstId, List<ChildNodeList> childNodeListAll, List<ConditionList> conditionListAll, List<ChildNode> chilNodeList) {
        if (childNode != null) {
            String nodeId = childNode.getNodeId();
            String prevId = childNode.getPrevId();
            boolean haschildNode = childNode.getChildNode() != null;
            boolean hasconditionNodes = childNode.getConditionNodes() != null;
            org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties properties = childNode.getProperties();
            //属性赋值
            assignment(properties);
            ChildNodeList childNodeList = new ChildNodeList();
            childNodeList.setProperties(properties);
            //定时器
            DateProperties model = JsonUtil.getJsonToBean(properties, DateProperties.class);
            childNodeList.setTimer(model);
            //自定义属性
            Custom customModel = new Custom();
            customModel.setType(childNode.getType());
            customModel.setNum("1");
            customModel.setFirstId(firstId);
            customModel.setChild(haschildNode);
            customModel.setChildNode(haschildNode == true ? childNode.getChildNode().getNodeId() : "");
            customModel.setNodeId(nodeId);
            customModel.setPrevId(prevId);
            //判断子节点数据是否还有分流节点,有的话保存分流节点id
            if (hasconditionNodes) {
                childNodeList.setConditionType(FlowCondition.CONDITION);
                List<ChildNode> conditionNodes = childNode.getConditionNodes().stream().filter(t -> t.getIsInterflow() != null || t.getIsBranchFlow() != null).collect(Collectors.toList());
                boolean isFlow = conditionNodes.size() > 0;
                if (isFlow) {
                    customModel.setFlow(isFlow);
                    boolean branchFlow = conditionNodes.stream().filter(t -> t.getIsBranchFlow() != null).count() > 0;
                    customModel.setBranchFlow(branchFlow);
                    childNodeList.setConditionType(branchFlow ? FlowCondition.ISBRANCHFLOW : FlowCondition.INTERFLOW);
                    List<String> flowIdAll = conditionNodes.stream().map(t -> t.getNodeId()).collect(Collectors.toList());
                    customModel.setFlowId(String.join(",", flowIdAll));
                }
            }
            childNodeList.setCustom(customModel);
            childNodeListAll.add(childNodeList);
            if (hasconditionNodes) {
                conditionList(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
            if (haschildNode) {
                getchildNode(childNode, firstId, childNodeListAll, conditionListAll, chilNodeList);
            }
        }
    }

    /**
     * 属性赋值
     *
     * @param properties
     */
    public static void assignment(Properties properties) {
    }

    /**
     * 替换文本值
     *
     * @param content
     * @param data
     * @return
     */
    public static String field(String content, Map<String, Object> data, String type) {
        String pattern = "[{]([^}]+)[}]";
        Pattern patternList = Pattern.compile(pattern);
        Matcher matcher = patternList.matcher(content);
        Map<String, List<String>> parameterMap = data(matcher, data);
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isNotEmpty(type)) {
            Map<String, String> datas = new HashMap<>();
            for (String key : parameterMap.keySet()) {
                datas.put(key, data.get(key) != null ? String.valueOf(data.get(key)) : "");
            }
            result.putAll(datas);
        } else {
            Map<String, Object> dataAll = new HashMap<>();
            for (String key : parameterMap.keySet()) {
                StringJoiner joiner = new StringJoiner(",");
                List<String> list = parameterMap.get(key);
                for (String id : list) {
                    joiner.add("'" + id + "'");
                }
                String value = joiner.toString();
                if (list.size() > 1) {
                    value = "SUM(" + joiner.toString() + ")";
                }
                dataAll.put(key, value);
            }
            result.putAll(dataAll);
        }
        StringSubstitutor strSubstitutor = new StringSubstitutor(result, "{", "}");
        String field = strSubstitutor.replace(content);
        return field;
    }

}
