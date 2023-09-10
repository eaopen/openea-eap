package org.openea.eap.extj.generater.util.common;

import lombok.Cleanup;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.DownloadCodeForm;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.form.model.form.DraftJsonModel;
import org.openea.eap.extj.form.model.form.FormDraftJsonModel;
import org.openea.eap.extj.model.visual.JnpfKeyConsts;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormCloumnUtil;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.model.visualJson.analysis.*;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * 功能流程公共工具
 *
 */
public class FunctionFormPublicUtil {

	public static FlowFormEntity exportFlowFormJson(VisualdevEntity entity, DownloadCodeForm downloadCodeForm) {
		FlowFormEntity flowFormEntity = new FlowFormEntity();
    	flowFormEntity.setId(entity.getId());
		flowFormEntity.setEnCode(entity.getEnCode());
		flowFormEntity.setFullName(entity.getFullName());
		flowFormEntity.setFlowType(1);
		flowFormEntity.setFormType(1);
		flowFormEntity.setCategory(entity.getCategory());
		flowFormEntity.setDescription(entity.getDescription());
		flowFormEntity.setSortCode(entity.getSortCode());
		flowFormEntity.setCreatorTime(entity.getCreatorTime());
		flowFormEntity.setCreatorUserId(entity.getCreatorUser());
		flowFormEntity.setTableJson(entity.getVisualTables());
		flowFormEntity.setDbLinkId(entity.getDbLinkId());
		//填写默认url
		String appUrl = "/pages/apply/" + downloadCodeForm.getClassName();
		flowFormEntity.setAppUrlAddress(appUrl);
		String webUrl = "extend/" + downloadCodeForm.getClassName().toLowerCase() + "/form.vue";
		flowFormEntity.setUrlAddress(webUrl);
		String downloadClassName = downloadCodeForm.getClassName().substring(0, 1).toUpperCase() +  downloadCodeForm.getClassName().substring(1);
		String interfaceUrl = "/api/" + downloadCodeForm.getModule() + "/" + downloadClassName;
		flowFormEntity.setInterfaceUrl(interfaceUrl);

		List<FormAllModel> formAllModel = new ArrayList<>();
		forDataMode(entity,formAllModel);
		List<FormAllModel> mastList = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
		List<DraftJsonModel> tempJson = new ArrayList<>();
		for (FormAllModel mastModel : mastList) {
			FieLdsModel fieLdsModel = mastModel.getFormColumnModel().getFieLdsModel();
			String model =  fieLdsModel.getVModel();
			ConfigModel config = fieLdsModel.getConfig();
			if(StringUtil.isNotEmpty(model)){
				DraftJsonModel engineModel = new DraftJsonModel();
				String label = config.getLabel();
				engineModel.setFiledId(model);
				engineModel.setFiledName(label);
				engineModel.setRequired(config.isRequired());
				engineModel.setJnpfKey(config.getJnpfKey());
				engineModel.setMultiple(fieLdsModel.getMultiple());
				tempJson.add(engineModel);
			}
		}
		List<FormAllModel> tableList = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
		List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(),TableModel.class);
		Map<String,String> tableListAll = tableNameRename(downloadCodeForm,tableModelList);
		for (FormAllModel model : tableList) {
			String table = model.getChildList().getTableName();
			String name = tableListAll.get(table);
			FormColumnTableModel childList = model.getChildList();
			String label = childList.getLabel();
			boolean required = childList.isRequired();
			DraftJsonModel engineModel = new DraftJsonModel();
			engineModel.setFiledId(name.toLowerCase()+"List");
			engineModel.setFiledName(label);
			engineModel.setRequired(required);
			tempJson.add(engineModel);
			for (FormColumnModel columnModel : model.getChildList().getChildList()) {
				String vModel = columnModel.getFieLdsModel().getVModel();
				String childLable = columnModel.getFieLdsModel().getConfig().getLabel();
				ConfigModel config = columnModel.getFieLdsModel().getConfig();
				if(StringUtil.isNotEmpty(vModel)) {
					DraftJsonModel childModel = new DraftJsonModel();
					childModel.setFiledId(name.toLowerCase() + "List-" + vModel);
					childModel.setFiledName(label+"-"+childLable);
					childModel.setRequired(config.isRequired());
					childModel.setJnpfKey(config.getJnpfKey());
					childModel.setMultiple(columnModel.getFieLdsModel().getMultiple());
					tempJson.add(childModel);
				}
			}
		}
		List<FormAllModel> mastTableList = formAllModel.stream().filter(t -> FormEnum.mastTable.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
		for (FormAllModel mastTableModel : mastTableList) {
			FormMastTableModel formMastTableModel = mastTableModel.getFormMastTableModel();
			FieLdsModel fieLdsModel = formMastTableModel.getMastTable().getFieLdsModel();
			String model =  formMastTableModel.getVModel();
			ConfigModel config = fieLdsModel.getConfig();
			if(StringUtil.isNotEmpty(model)){
				DraftJsonModel engineModel = new DraftJsonModel();
				String label = config.getLabel();
				engineModel.setFiledId(model);
				engineModel.setFiledName(label);
				engineModel.setRequired(config.isRequired());
				engineModel.setJnpfKey(config.getJnpfKey());
				engineModel.setMultiple(fieLdsModel.getMultiple());
				tempJson.add(engineModel);
			}
		}
		FormDraftJsonModel draftJsonModel = new FormDraftJsonModel();
		String Tem = JsonUtil.getObjectToString(tempJson);
		flowFormEntity.setPropertyJson(Tem);
		tableJson(tableList, flowFormEntity, tableListAll);
		draftJsonModel.setDraftJson(Tem);
		draftJsonModel.setTableJson(entity.getVisualTables());
		flowFormEntity.setDraftJson(JsonUtil.getObjectToString(draftJsonModel));
		return flowFormEntity;
	}


	/**
	 * 创建文件
	 * @param data
	 * @param path
	 */
	public static void createFile(String data, String path) {
		try {
			File file = new File(path+File.separator+"flow."+ ModuleTypeEnum.FLOW_FLOWENGINE.getTableName());
			boolean b = file.createNewFile();
			if(b) {
				@Cleanup Writer out = new FileWriter(file);
				out.write(data);
				out.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 封装数据
	 *
	 * @param entity
	 * @param formAllModel
	 */
	private static void forDataMode(VisualdevEntity entity, List<FormAllModel> formAllModel) {
		//formTempJson
		FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
		List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
		List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getVisualTables(), TableModel.class);
		RecursionForm recursionForm = new RecursionForm(list,tableModelList);
		FormCloumnUtil.recursionForm(recursionForm, formAllModel);
	}

	public static Map<String,String> tableNameRename(DownloadCodeForm downloadCodeForm, List<TableModel> tableModelList){
		Map<String, String> tableNameMap = new HashMap<>(16);
		int i = 0;
		for (TableModel tableModel : tableModelList) {
			if ("0".equals(tableModel.getTypeId())) {
				String[] subClassName = downloadCodeForm.getSubClassName().split(",");
				tableNameMap.put(tableModel.getTable(), subClassName[i]);
				i++;
			}else {
				tableNameMap.put(tableModel.getTable(),downloadCodeForm.getClassName());
			}
		}
		return tableNameMap;
	}

    private static void tableJson(List<FormAllModel> tableList, VisualdevEntity entity, Map<String, String> tableListAll) {
//        String json = entity.getFlowTemplateJson();
//        for (FormAllModel model : tableList) {
//            String table = model.getChildList().getTableName();
//            String tableModel = model.getChildList().getTableModel();
//            String name = tableListAll.get(table);
//            json = json.replaceAll(tableModel, name.toLowerCase() + "List");
//        }
//        entity.setFlowTemplateJson(json);
//    }
		}
			private static void tableJson(List<FormAllModel> tableList, FlowFormEntity entity, Map<String, String> tableListAll) {
        String json = entity.getPropertyJson();
        for (FormAllModel model : tableList) {
            String table = model.getChildList().getTableName();
            String tableModel = model.getChildList().getTableModel();
            String name = tableListAll.get(table);
            json = json.replaceAll(tableModel, name.toLowerCase() + "List");
        }
        entity.setPropertyJson(json);
    }

    public static void getTableModels(List<FieLdsModel> fieLdsModelList, List<FieLdsModel> tableModelFields){
			for (FieLdsModel fieLdsModel : fieLdsModelList){
				String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
				if (JnpfKeyConsts.CHILD_TABLE.equals(jnpfKey)){
					tableModelFields.add(fieLdsModel);
				} else {
					List<FieLdsModel> children = fieLdsModel.getConfig().getChildren();
					if (children!= null){
						getTableModels(children, tableModelFields);
					} else {
						continue;
					}
				}
			}

		}

}
