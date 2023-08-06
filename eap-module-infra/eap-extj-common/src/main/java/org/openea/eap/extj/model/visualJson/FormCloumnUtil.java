package org.openea.eap.extj.model.visualJson;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import org.openea.eap.extj.model.visualJson.analysis.*;
import org.openea.eap.extj.model.visualJson.config.ConfigModel;
import org.openea.eap.extj.model.visualJson.props.PropsBeanModel;
import org.openea.eap.extj.model.visualJson.props.PropsModel;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.util.StringUtils;

public class FormCloumnUtil {
    public FormCloumnUtil() {
    }

    public static void recursionForm(RecursionForm recursionForm, List<FormAllModel> formAllModel) {
        List<TableModel> tableModelList = recursionForm.getTableModelList();
        List<FieLdsModel> list = recursionForm.getList();
        Iterator var4 = list.iterator();

        while(true) {
            while(var4.hasNext()) {
                FieLdsModel fieLdsModel = (FieLdsModel)var4.next();
                FormAllModel start = new FormAllModel();
                FormAllModel end = new FormAllModel();
                ConfigModel config = fieLdsModel.getConfig();
                String tipLable = StringUtil.isNotEmpty(config.getTipLabel()) ? config.getTipLabel().trim().replaceAll("\r", "").replaceAll("\n", " ") : config.getTipLabel();
                config.setTipLabel(tipLable);
                String visibility = config.getVisibility();
                multipleChoices(config);
                String jnpfkey = config.getJnpfKey();
                List<FieLdsModel> childrenList = config.getChildren();
                List<String> keyList = new ArrayList() {
                    {
                        this.add(FormEnum.collapseItem.getMessage());
                        this.add(FormEnum.collapseItem.getMessage());
                        this.add(FormEnum.row.getMessage());
                        this.add(FormEnum.card.getMessage());
                        this.add(FormEnum.tab.getMessage());
                        this.add(FormEnum.collapse.getMessage());
                        this.add(FormEnum.tableGrid.getMessage());
                        this.add(FormEnum.tableGridTr.getMessage());
                        this.add(FormEnum.tableGridTd.getMessage());
                    }
                };
                boolean isEndJnpfKey = StringUtils.isEmpty(jnpfkey) || FormEnum.collapseItem.getMessage().equals(jnpfkey) || FormEnum.tabItem.getMessage().equals(jnpfkey);
                boolean isName = ObjectUtils.isNotEmpty(fieLdsModel.getName());
                if (!keyList.contains(jnpfkey) && !isEndJnpfKey) {
                    if (FormEnum.table.getMessage().equals(jnpfkey)) {
                        tableModel(fieLdsModel, formAllModel);
                    } else if (FormEnum.isModel(jnpfkey)) {
                        FormModel formModel = (FormModel) JsonUtil.getJsonToBean(fieLdsModel, FormModel.class);
                        formModel.setVisibility(fieLdsModel.getConfig().getVisibility());
                        start.setJnpfKey(jnpfkey);
                        start.setFormModel(formModel);
                        formAllModel.add(start);
                    } else {
                        model(fieLdsModel, formAllModel, tableModelList);
                    }
                } else {
                    String key = isEndJnpfKey ? (isName ? FormEnum.collapse.getMessage() : FormEnum.tab.getMessage()) : jnpfkey;
                    FormModel formModel = (FormModel)JsonUtil.getJsonToBean(fieLdsModel, FormModel.class);
                    formModel.setSpan(config.getSpan());
                    formModel.setActive(config.getActive());
                    formModel.setChildNum(config.getChildNum());
                    formModel.setModel(config.getModel());
                    formModel.setVisibility(config.getVisibility());
                    formModel.setMerged(config.getMerged());
                    formModel.setColspan(config.getColspan());
                    formModel.setRowspan(config.getRowspan());
                    formModel.setRowType(config.getRowType());
                    formModel.setBorderColor(config.getBorderColor());
                    formModel.setBorderType(config.getBorderType());
                    formModel.setBorderWidth(config.getBorderWidth());
                    String outermost = !isEndJnpfKey ? "0" : "1";
                    if (FormEnum.tab.getMessage().equals(key) || FormEnum.collapse.getMessage().equals(key)) {
                        if (!isEndJnpfKey) {
                            String chidModel = "active" + RandomUtil.enUuId();
                            formModel.setModel(chidModel);

                            for(int i = 0; i < childrenList.size(); ++i) {
                                FieLdsModel childModel = (FieLdsModel)childrenList.get(i);
                                ConfigModel childConfig = childModel.getConfig();
                                childConfig.setVisibility(visibility);
                                childConfig.setModel(chidModel);
                                childConfig.setChildNum(i);
                                multipleChoices(childConfig);
                                childModel.setConfig(childConfig);
                            }

                            formModel.setChildren(childrenList);
                        }

                        formModel.setOutermost(outermost);
                    }

                    start.setJnpfKey(key);
                    start.setFormModel(formModel);
                    formAllModel.add(start);
                    RecursionForm recursion = new RecursionForm(childrenList, tableModelList);
                    recursionForm(recursion, formAllModel);
                    end.setIsEnd("1");
                    end.setJnpfKey(key);
                    FormModel endFormModel = new FormModel();
                    endFormModel.setOutermost(outermost);
                    endFormModel.setConfig(config);
                    end.setFormModel(endFormModel);
                    formAllModel.add(end);
                }
            }

            var4 = formAllModel.iterator();

            while(var4.hasNext()) {
                FormAllModel formModel = (FormAllModel)var4.next();
                if (FormEnum.mast.getMessage().equals(formModel.getJnpfKey())) {
                    setRelationFieldAttr(formAllModel, formModel.getFormColumnModel().getFieLdsModel());
                } else if (FormEnum.mastTable.getMessage().equals(formModel.getJnpfKey())) {
                    setRelationFieldAttr(formAllModel, formModel.getFormMastTableModel().getMastTable().getFieLdsModel());
                }
            }

            return;
        }
    }

    private static ConfigModel multipleChoices(ConfigModel configModel) {
        String visibility = configModel.getVisibility();
        if (Objects.nonNull(visibility)) {
            configModel.setApp(visibility.contains("app"));
            configModel.setPc(visibility.contains("pc"));
        }

        return configModel;
    }

    private static void model(FieLdsModel fieLdsModel, List<FormAllModel> formAllModel, List<TableModel> tableModelList) {
        FormColumnModel mastModel = formModel(fieLdsModel);
        FormAllModel formModel = new FormAllModel();
        formModel.setJnpfKey(FormEnum.mast.getMessage());
        formModel.setFormColumnModel(mastModel);
        if (tableModelList.size() > 0) {
            TableModel tableModel = (TableModel)tableModelList.stream().filter((t) -> {
                return t.getTable().equals(fieLdsModel.getConfig().getTableName());
            }).findFirst().orElse((TableModel)null);
            if (tableModel == null) {
                Optional<TableModel> first = tableModelList.stream().filter((t) -> {
                    return "1".equals(t.getTypeId());
                }).findFirst();
                if (!first.isPresent()) {
                    throw new RuntimeException("未找到主表信息");
                }

                tableModel = (TableModel)first.get();
            }

            String type = tableModel.getTypeId();
            if ("1".equals(type)) {
                mastModel.getFieLdsModel().getConfig().setTableName(tableModel.getTable());
                formModel.setFormColumnModel(mastModel);
                formAllModel.add(formModel);
            } else {
                mastTable(tableModel, fieLdsModel, formAllModel);
            }
        } else {
            formAllModel.add(formModel);
        }

    }

    private static void mastTable(TableModel tableModel, FieLdsModel fieLdsModel, List<FormAllModel> formAllModel) {
        FormMastTableModel childModel = new FormMastTableModel();
        String vModel = fieLdsModel.getVModel();
        List<TableFields> tableFieldsList = tableModel.getFields();
        String mastKey = "jnpf_" + tableModel.getTable() + "_jnpf_";
        TableFields tableFields = (TableFields)tableFieldsList.stream().filter((t) -> {
            return StringUtil.isNotEmpty(vModel) && vModel.equals(mastKey + t.getField());
        }).findFirst().orElse((TableFields)null);
        FormAllModel formModel = new FormAllModel();
        formModel.setJnpfKey(FormEnum.mastTable.getMessage());
        if (tableFields != null) {
            childModel.setTable(tableModel.getTable());
            formModel.setFormMastTableModel(childModel);
            childModel.setField(tableFields.getField());
            childModel.setVModel(vModel);
        }

        FormColumnModel mastTable = formModel(fieLdsModel);
        childModel.setMastTable(mastTable);
        formAllModel.add(formModel);
    }

    private static void tableModel(FieLdsModel model, List<FormAllModel> formAllModel) {
        List<FormColumnModel> childList = new ArrayList();
        ConfigModel config = model.getConfig();
        List<FieLdsModel> childModelList = config.getChildren();
        String table = model.getVModel();
        List<String> summaryField = StringUtil.isNotEmpty(model.getSummaryField()) ? JsonUtil.getJsonToList(model.getSummaryField(), String.class) : new ArrayList();
        Map<String, String> summaryName = new HashMap();
        Iterator var8 = childModelList.iterator();

        while(var8.hasNext()) {
            FieLdsModel childmodel = (FieLdsModel)var8.next();
            if (childmodel.getProps() != null) {
                PropsBeanModel beanModel = (PropsBeanModel)JsonUtil.getJsonToBean(childmodel.getProps().getProps(), PropsBeanModel.class);
                PropsModel propsModel = new PropsModel();
                propsModel.setProps(childmodel.getProps().getProps());
                propsModel.setPropsModel(beanModel);
                childmodel.setProps(propsModel);
            }

            FormColumnModel childModel = formModel(childmodel);
            boolean isSummary = ((List)summaryField).contains(childmodel.getVModel());
            if (isSummary) {
                summaryName.put(childmodel.getVModel(), childmodel.getConfig().getLabel());
            }

            relationModel(childModelList, childmodel);
            childList.add(childModel);
        }

        multipleChoices(config);
        FormColumnTableModel tableModel = (FormColumnTableModel)JsonUtil.getJsonToBean(config, FormColumnTableModel.class);
        String tipLable = StringUtil.isNotEmpty(tableModel.getTipLabel()) ? tableModel.getTipLabel().trim().replaceAll("\r", "").replaceAll("\n", " ") : tableModel.getTipLabel();
        tableModel.setTipLabel(tipLable);
        tableModel.setActionText(StringUtil.isNotEmpty(model.getActionText()) ? model.getActionText() : "新增");
        tableModel.setTableModel(table);
        tableModel.setChildList(childList);
        tableModel.setShowSummary(model.getShowSummary());
        tableModel.setSummaryField(JsonUtil.getObjectToString(summaryField));
        tableModel.setSummaryFieldName(JsonUtil.getObjectToString(summaryName));
        tableModel.setThousands(model.isThousands());
        tableModel.setVisibility(config.getVisibility());
        tableModel.setAddType(model.getAddType());
        tableModel.setAddTableConf(model.getAddTableConf());
        FormAllModel formModel = new FormAllModel();
        formModel.setJnpfKey(FormEnum.table.getMessage());
        formModel.setChildList(tableModel);
        formAllModel.add(formModel);
    }

    private static void relationModel(List<FieLdsModel> childModelList, FieLdsModel childmodel) {
        ConfigModel config = childmodel.getConfig();
        String jnpfkey = config.getJnpfKey();
        String startRelationField = config.getStartRelationField();
        String endRelationField = config.getEndRelationField();
        String childRelationField = childmodel.getRelationField();
        if (FormEnum.relationFormAttr.getMessage().equals(jnpfkey) || FormEnum.popupAttr.getMessage().equals(jnpfkey)) {
            String relationField = childmodel.getRelationField().split("_jnpfTable_")[0];
            FieLdsModel child = (FieLdsModel)childModelList.stream().filter((t) -> {
                return relationField.equals(t.getVModel());
            }).findFirst().orElse((FieLdsModel)null);
            if (child != null) {
                childmodel.setInterfaceId(child.getInterfaceId());
                childmodel.setModelId(child.getModelId());
                childmodel.setPropsValue(child.getPropsValue());
                childmodel.setRelationField(relationField);
            }
        }

        String[] relationField;
        if ("userSelect".equals(jnpfkey) && StringUtil.isNotEmpty(childRelationField)) {
            relationField = childRelationField.split("-");
            if (relationField.length > 1) {
                childmodel.setRelationField(relationField[1]);
            }

            childmodel.setRelationChild(relationField.length > 1);
        }

        if ("date".equals(jnpfkey) || "time".equals(jnpfkey)) {
            if (StringUtil.isNotEmpty(startRelationField)) {
                relationField = startRelationField.split("-");
                if (relationField.length > 1) {
                    childmodel.getConfig().setStartRelationField(relationField[1]);
                }

                childmodel.getConfig().setStartChild(relationField.length > 1);
            }

            if (StringUtil.isNotEmpty(endRelationField)) {
                relationField = endRelationField.split("-");
                if (relationField.length > 1) {
                    childmodel.getConfig().setEndRelationField(relationField[1]);
                }

                childmodel.getConfig().setEndRChild(relationField.length > 1);
            }
        }

    }

    private static FormColumnModel formModel(FieLdsModel model) {
        ConfigModel configModel = model.getConfig();
        multipleChoices(configModel);
        if (configModel.getDefaultValue() instanceof String) {
            configModel.setValueType("String");
        }

        if (configModel.getDefaultValue() == null) {
            configModel.setValueType("undefined");
        }

        FormColumnModel formColumnModel = new FormColumnModel();
        if ("cascader".equals(configModel.getJnpfKey())) {
            PropsBeanModel propsMap = (PropsBeanModel)JsonUtil.getJsonToBean(model.getProps().getProps(), PropsBeanModel.class);
            model.setMultiple(propsMap.getMultiple());
        }

        formColumnModel.setFieLdsModel(model);
        return formColumnModel;
    }

    public static boolean repetition(RecursionForm recursionForm, List<FormAllModel> formAllModel) {
        boolean flag = false;
        List<TableModel> tableModelList = recursionForm.getTableModelList();
        recursionForm(recursionForm, formAllModel);
        if (tableModelList.size() > 0) {
            List<FormAllModel> tables = (List)formAllModel.stream().filter((t) -> {
                return FormEnum.table.getMessage().equals(t.getJnpfKey());
            }).collect(Collectors.toList());
            List<FormAllModel> mastTable = (List)formAllModel.stream().filter((t) -> {
                return FormEnum.mastTable.getMessage().equals(t.getJnpfKey());
            }).collect(Collectors.toList());
            List<String> tableList = (List)tables.stream().map((t) -> {
                return t.getChildList().getTableName();
            }).collect(Collectors.toList());
            List<String> mastTableList = (List)mastTable.stream().map((t) -> {
                return t.getFormMastTableModel().getTable();
            }).collect(Collectors.toList());
            flag = tableList.stream().filter((item) -> {
                return mastTableList.contains(item);
            }).count() > 0L;
        }

        return flag;
    }

    private static void setRelationFieldAttr(List<FormAllModel> formAllModel, FieLdsModel formModel) {
        String jnpfkey = formModel.getConfig().getJnpfKey();
        if (FormEnum.relationFormAttr.getMessage().equals(jnpfkey) || FormEnum.popupAttr.getMessage().equals(jnpfkey)) {
            List<FieLdsModel> partenList = new ArrayList();
            partenList.addAll((Collection)formAllModel.stream().filter((t) -> {
                return t.getFormColumnModel() != null;
            }).map((t) -> {
                return t.getFormColumnModel().getFieLdsModel();
            }).collect(Collectors.toList()));
            partenList.addAll((Collection)formAllModel.stream().filter((t) -> {
                return t.getFormMastTableModel() != null;
            }).map((t) -> {
                return t.getFormMastTableModel().getMastTable().getFieLdsModel();
            }).collect(Collectors.toList()));
            String relationField = formModel.getRelationField().split("_jnpfTable_")[0];
            FieLdsModel parten = (FieLdsModel)partenList.stream().filter((t) -> {
                return relationField.equals(t.getVModel());
            }).findFirst().orElse((FieLdsModel)null);
            if (parten != null) {
                formModel.setInterfaceId(parten.getInterfaceId());
                formModel.setModelId(parten.getModelId());
                formModel.setPropsValue(parten.getPropsValue());
                formModel.setRelationField(parten.getVModel());
            }
        }

    }
}