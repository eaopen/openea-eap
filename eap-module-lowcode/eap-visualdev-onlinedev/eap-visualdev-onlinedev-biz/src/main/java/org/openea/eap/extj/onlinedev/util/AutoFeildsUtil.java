package org.openea.eap.extj.onlinedev.util;

import cn.hutool.core.util.ObjectUtil;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.entity.PositionEntity;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.OrganizeService;
import org.openea.eap.extj.permission.service.PositionService;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.permission.util.PermissionUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.JsonUtilEx;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.context.SpringContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 处理自动生成字段
 *
 */
public class AutoFeildsUtil {
    private static OrganizeService organizeService;
    private static UserService userService;
    private static PositionService positionService;


    //初始化
    public static void init() {
        userService = SpringContext.getBean(UserService.class);
        organizeService = SpringContext.getBean(OrganizeService.class);
        positionService=SpringContext.getBean(PositionService.class);
    }

    /**
     * 列表系统自动生成字段转换
     *
     * @return String
     */
    public static String autoFeilds(List<FieLdsModel> fieLdsModelList, String data) {
        init();
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            Map<String, Object> dataMap = JsonUtil.stringToMap(data);
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                if (fieLdsModel.getVModel().equals(entry.getKey())) {
                    String jnpfKeyType = fieLdsModel.getConfig().getJnpfKey();
                    switch (jnpfKeyType) {
                        case JnpfKeyConsts.CURRORGANIZE:
                        case JnpfKeyConsts.CURRDEPT:
                            List<OrganizeEntity> orgMapList = organizeService.getOrgRedisList();
                            for (OrganizeEntity organizeEntity : orgMapList) {
                                if (String.valueOf(entry.getValue()).equals(organizeEntity.getId())) {
                                    if("all".equals(fieLdsModel.getShowLevel())){
                                        entry.setValue(PermissionUtil.getLinkInfoByOrgId(organizeEntity.getId(), organizeService, false));
                                    }else {
                                        entry.setValue(organizeEntity.getFullName());
                                    }
                                }
                            }
                            break;
                        case JnpfKeyConsts.CREATEUSER:
                        case JnpfKeyConsts.MODIFYUSER:
                            UserEntity userCreEntity = userService.getInfo(String.valueOf(entry.getValue()));
                            if (userCreEntity != null) {
                                entry.setValue(userCreEntity.getRealName());
                            }
                            break;
                        case JnpfKeyConsts.CURRPOSITION:
                            String[] curPos = String.valueOf(entry.getValue()).split(",");
                            List<String> curPosList = new ArrayList<>();
                            for (String pos : curPos){
                                PositionEntity posEntity = positionService.getInfo(pos);
                                String posName = Objects.nonNull(posEntity) ? posEntity.getFullName() : "";
                                curPosList.add(posName);
                            }
                            entry.setValue(curPosList.stream().collect(Collectors.joining(",")));
                            break;
                        case JnpfKeyConsts.CREATETIME:
                        case JnpfKeyConsts.MODIFYTIME:
                            if (ObjectUtil.isNotEmpty(entry.getValue())){
                                String dateStr=String.valueOf(entry.getValue());
                                dateStr=dateStr.length()>19?dateStr.substring(0,19):dateStr;
                                entry.setValue(dateStr);
                            }else {
                                entry.setValue(null);
                            }
                            break;
                        default:
                    }
                }
            }
            data = JsonUtilEx.getObjectToString(dataMap);
        }
        return data;
    }

    public static FieLdsModel getTreeRelationSearch(List<FieLdsModel> FieLdsModels, String treeRelationField) {
        FieLdsModel fieLdsModel = new FieLdsModel();
        boolean treeIsChild = treeRelationField.toLowerCase().contains("tablefield");
        if (treeIsChild){
            String tableField = treeRelationField.substring(0,treeRelationField.indexOf("-"));
            String relationVmodel = treeRelationField.substring(treeRelationField.indexOf("-")+1);
            List<FieLdsModel> allFields = new ArrayList<>();
            recursionFields(FieLdsModels,allFields);
//            List<FieLdsModel> childFields = FieLdsModels.stream().filter(fieLd -> fieLd.getVModel().equals(tableField)).map(f -> f.getConfig().getChildren()).findFirst().orElse(new ArrayList<>());
            fieLdsModel = allFields.stream().filter(swap->relationVmodel.equalsIgnoreCase(swap.getVModel())
                    &&tableField.equals(swap.getConfig().getParentVModel())).findFirst().orElse(null);
        } else {
            //递归出所有表单控件从中去除左侧树的控件属性
            List<FieLdsModel> allFields = new ArrayList<>();
            recursionFields(FieLdsModels,allFields);
            fieLdsModel = allFields.stream().filter(swap -> treeRelationField.equalsIgnoreCase(swap.getVModel())).findFirst().orElse(null);
        }
        return fieLdsModel;
    }

    private static void recursionFields(List<FieLdsModel> fieLdsModelList,List<FieLdsModel> allFields){
        for (FieLdsModel fieLdsModel : fieLdsModelList){
            if (fieLdsModel.getConfig().getChildren()!=null){
                recursionFields(fieLdsModel.getConfig().getChildren(),allFields);
            }else {
                if (StringUtil.isNotEmpty(fieLdsModel.getVModel())){
                    allFields.add(fieLdsModel);
                }
            }
        }
    }
}
