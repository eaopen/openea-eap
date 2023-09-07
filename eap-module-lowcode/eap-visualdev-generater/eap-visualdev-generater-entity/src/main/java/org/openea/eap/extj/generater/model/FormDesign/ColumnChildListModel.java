package org.openea.eap.extj.generater.model.FormDesign;

import lombok.Data;

import java.util.List;

@Data
public class ColumnChildListModel {
	private String label;
	private String tableField;
	private String vModel;
	private List<ColumnListModel> fields;
}
