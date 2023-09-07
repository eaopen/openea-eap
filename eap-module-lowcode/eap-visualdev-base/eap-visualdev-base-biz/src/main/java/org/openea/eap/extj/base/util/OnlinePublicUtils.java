package org.openea.eap.extj.base.util;

import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.entity.VisualdevReleaseEntity;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.base.model.VisualWebTypeEnum;
import org.openea.eap.extj.model.visualJson.ColumnDataModel;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.util.JsonUtil;

import java.util.Map;

public class OnlinePublicUtils {
    public static boolean mapCompar(Map<String, Object> conditionMap, Map<String, Object> hashMap2, Map<String, Boolean> conditionConfig) {
        boolean isChange = false;
        for (String key : conditionMap.keySet()) {
            String m1value = conditionMap.get(key) == null ? "" : conditionMap.get(key).toString();
            String m2value = hashMap2.get(key) == null ? "" : (String) hashMap2.get(key);
            boolean flag = conditionConfig != null && conditionConfig.get(key) != null ? conditionConfig.get(key) : false;
            if (flag && m1value.equals(m2value)) {
                isChange = true;
            } else if (!flag && m2value.indexOf(m1value) >= 0) {
                isChange = true;
            } else {
                return false;//必须条件全满足才会true
            }
        }
        return isChange;
    }
    public static VisualDevJsonModel getVisualJsonModel(VisualdevEntity entity){
        VisualDevJsonModel jsonModel = new VisualDevJsonModel();
        jsonModel.setColumnData(JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class));
        jsonModel.setAppColumnData(JsonUtil.getJsonToBean(entity.getAppColumnData(), ColumnDataModel.class));
        FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        jsonModel.setFormData(formDataModel);
        if(!VisualWebTypeEnum.DATA_VIEW.getType().equals(entity.getWebType())){
            jsonModel.setFormListModels(JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class));
        }
        jsonModel.setVisualTables(JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class));
        jsonModel.setId(entity.getId());
        jsonModel.setDbLinkId(entity.getDbLinkId());
        jsonModel.setFullName(entity.getFullName());
        jsonModel.setType(entity.getType());
        jsonModel.setWebType(entity.getWebType());
        jsonModel.setFlowEnable(entity.getEnableFlow() == 1);
        return jsonModel;
    }
    public static VisualDevJsonModel getVisualJsonModel(VisualdevReleaseEntity entity){
        VisualDevJsonModel jsonModel = new VisualDevJsonModel();
        if(entity.getColumnData()!=null){
            jsonModel.setColumnData(JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class));
        }
        if(entity.getAppColumnData()!=null){
            jsonModel.setAppColumnData(JsonUtil.getJsonToBean(entity.getAppColumnData(), ColumnDataModel.class));
        }
        FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        jsonModel.setFormData(formDataModel);
        if(!VisualWebTypeEnum.DATA_VIEW.getType().equals(entity.getWebType())){
            jsonModel.setFormListModels(JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class));
        }
        jsonModel.setVisualTables(JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class));
        jsonModel.setId(entity.getId());
        jsonModel.setDbLinkId(entity.getDbLinkId());
        jsonModel.setFullName(entity.getFullName());
        jsonModel.setType(entity.getType());
        jsonModel.setWebType(entity.getWebType());
        jsonModel.setFlowEnable(entity.getEnableFlow() == 1);
        return jsonModel;
    }
}
