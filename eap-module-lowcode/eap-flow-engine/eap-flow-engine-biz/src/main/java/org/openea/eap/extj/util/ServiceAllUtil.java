package org.openea.eap.extj.util;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.service.*;
import org.openea.eap.extj.base.util.SentMessageUtil;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.extend.service.BillRuleService;
import org.openea.eap.extj.message.model.message.SentMessageForm;
import org.openea.eap.extj.message.service.SendMessageConfigService;
import org.openea.eap.extj.permission.entity.*;
import org.openea.eap.extj.permission.service.*;
import org.openea.eap.extj.form.service.FlowFormRelationService;
import org.openea.eap.extj.form.service.FlowFormService;
import org.openea.eap.extj.form.service.FormDataService;
import org.openea.eap.extj.util.enums.DictionaryDataEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 */
@Component
@DS("")
public class ServiceAllUtil {

    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private BillRuleService billRuleService;
    @Autowired
    private DataInterfaceService dataInterfaceService;
    @Autowired
    private SentMessageUtil sentMessageUtil;
    @Autowired
    private FormDataService formDataService;
    @Autowired
    private FlowFormService flowFormService;
    @Autowired
    private FlowFormRelationService flowFormRelationService;
    @Autowired
    private SendMessageConfigService sendMessageConfigService;

    //--------------------------------代码生成器方法------------------------------
    public DbLinkEntity getDbLink(String dbLink) {
        DbLinkEntity link = StringUtil.isNotEmpty(dbLink) ? dblinkService.getInfo(dbLink) : null;
        return link;
    }

    public PositionEntity getPositionInfo(String id) {
        List<PositionEntity> positionList = getPositionName(new ArrayList() {{
            add(id);
        }});
        return positionList.size() > 0 ? positionList.get(0) : null;
    }

    public String getBillNumber(String billNumber) {
        String data = StringUtil.isNotEmpty(billNumber) ? billRuleService.getBillNumber(billNumber,false) : null;
        return data;
    }
    //--------------------------------数据字典------------------------------
    public List<DictionaryDataEntity> getDiList() {
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getList(dictionaryTypeService.getInfoByEnCode(DictionaryDataEnum.FLOWWOEK_ENGINE.getDictionaryTypeId()).getId());
        return dictionList;
    }

    public List<DictionaryDataEntity> getDictionName(List<String> id) {
        List<DictionaryDataEntity> dictionList = dictionaryDataService.getDictionName(id);
        return dictionList;
    }

    //--------------------------------用户关系表------------------------------
    public List<UserRelationEntity> getListByUserIdAll(List<String> id) {
        List<UserRelationEntity> list = userRelationService.getListByUserIdAll(id);
        return list;
    }

    public List<UserRelationEntity> getListByObjectIdAll(List<String> id) {
        List<UserRelationEntity> list = userRelationService.getListByObjectIdAll(id);
        return list;
    }

    public String getAdmin() {
        UserEntity admin = userService.getUserByAccount("admin");
        return admin.getId();
    }

    //--------------------------------用户------------------------------
    public List<UserEntity> getUserName(List<String> id) {
        List<UserEntity> list = getUserName(id, false);
        return list;
    }

    public List<UserEntity> getListByManagerId(String managerId) {
        List<UserEntity> list = StringUtil.isNotEmpty(managerId) ? userService.getListByManagerId(managerId, null) : new ArrayList<>();
        return list;
    }

    public List<UserEntity> getUserName(List<String> id, boolean enableMark) {
        List<UserEntity> list = userService.getUserName(id);
        if (enableMark) {
            list = list.stream().filter(t -> t.getEnabledMark() != 0).collect(Collectors.toList());
        }
        return list;
    }

    public List<UserEntity> getUserName(List<String> id, Pagination pagination) {
        List<UserEntity> list = userService.getUserName(id, pagination);
        return list;
    }

    public UserEntity getUserInfo(String id) {
        UserEntity entity = null;
        if (StringUtil.isNotEmpty(id)) {
            entity = id.equalsIgnoreCase("admin") ? userService.getUserByAccount(id) : userService.getInfo(id);
        }
        return entity;
    }

    //--------------------------------单据规则------------------------------

    public void useBillNumber(String enCode) {
        billRuleService.useBillNumber(enCode);
    }

    //--------------------------------角色------------------------------
    public List<RoleEntity> getListByIds(List<String> id) {
        List<RoleEntity> list = roleService.getListByIds(id);
        return list;
    }

    //--------------------------------组织------------------------------
    public List<OrganizeEntity> getOrganizeName(List<String> id) {
        List<OrganizeEntity> list = organizeService.getOrganizeName(id);
        return list;
    }

    public OrganizeEntity getOrganizeInfo(String id) {
        OrganizeEntity entity = StringUtil.isNotEmpty(id) ? organizeService.getInfo(id) : null;
        return entity;
    }

    public List<OrganizeEntity> getOrganizeId(String organizeId) {
        List<OrganizeEntity> organizeList = new ArrayList<>();
        organizeService.getOrganizeId(organizeId, organizeList);
        Collections.reverse(organizeList);
        return organizeList;
    }

    public List<OrganizeEntity> getDepartmentAll(String organizeId) {
        List<OrganizeEntity> departmentAll = organizeService.getDepartmentAll(organizeId);
        return departmentAll;
    }

    //--------------------------------岗位------------------------------
    public List<PositionEntity> getPositionName(List<String> id) {
        List<PositionEntity> list = positionService.getPositionName(id);
        return list;
    }

    //--------------------------------远端------------------------------
    public void infoToId(String interId, Map<String, String> parameterMap) {
        dataInterfaceService.infoToId(interId, null, parameterMap);
    }

    //--------------------------------发送消息------------------------------
    public void sendMessage(List<SentMessageForm> messageListAll) {
        for (SentMessageForm messageForm : messageListAll) {
            if (messageForm.getToUserIds().size() > 0) {
                if (messageForm.isSysMessage()) {
                    sentMessageUtil.sendMessage(messageForm);
                }
            }
        }
    }

    public void updateSendConfigUsed(String id, List<String> idList) {
        sendMessageConfigService.updateUsed(id, idList);
    }

    public void sendDelegateMsg(List<SentMessageForm> messageListAll){
        for (SentMessageForm messageForm : messageListAll) {
            if (messageForm.getToUserIds().size() > 0) {
                sentMessageUtil.sendDelegateMsg(messageForm);
            }
        }
    }

    //------------------------------表单数据-------------------------------
    public void createOrUpdate(String formId, String id, Map<String, Object> map) throws WorkFlowException {
        this.createOrUpdateDelegateUser(formId, id, map, null);
    }

    public void createOrUpdateDelegateUser(String formId, String id, Map<String, Object> map, UserEntity delegetUser) throws WorkFlowException {
        formDataService.saveOrUpdate(formId, id, map, delegetUser);
    }

    public Map<String, Object> infoData(String formId, String id) {
        Map<String, Object> dataAll = new HashMap<>();
        if (StringUtil.isNotEmpty(formId) && StringUtil.isNotEmpty(id)) {
            Map<String, Object> info = formDataService.info(formId, id);
            Map<String, Object> data = new HashMap(16) {{
                putAll(info);
            }};
            for (String key : info.keySet()) {
                if (info.get(key) instanceof Map) {
                    Map<String, Object> mastTableMap = (Map<String, Object>) info.get(key);
                    for (String mastKey : mastTableMap.keySet()) {
                        data.put("jnpf_" + key + "_jnpf_" + mastKey, mastTableMap.get(mastKey));
                    }
                }
            }
            dataAll.putAll(data);
        }
        return dataAll;
    }

    //------------------------------表单对象-------------------------------
    public FlowFormEntity getForm(String id) {
        FlowFormEntity form = StringUtil.isNotEmpty(id) ? flowFormService.getById(id) : null;
        return form;
    }

    public List<FlowFormEntity> getFlowIdList(String id) {
        List<FlowFormEntity> list = StringUtil.isNotEmpty(id) ? flowFormService.getFlowIdList(id) : new ArrayList<>();
        return list;
    }

    public void updateForm(FlowFormEntity entity) {
        flowFormService.updateForm(entity);
    }

    public void formIdList(List<String> formId, String tempJsonId) {
        flowFormRelationService.saveFlowIdByFormIds(tempJsonId, formId);
    }

    public void deleteFormId(String tempJsonId) {
        flowFormRelationService.saveFlowIdByFormIds(tempJsonId, new ArrayList<>());
    }

}
