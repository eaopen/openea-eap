package org.openea.eap.extj.onlinedev.model;

import lombok.Data;

import java.util.List;

/**
 *
 *

 */
@Data
public class ExcelImFieldModel {
	private String id;
	private String fullName;
	private List<ExcelImFieldModel> children;

	public ExcelImFieldModel(String id, String fullName, List<ExcelImFieldModel> children) {
		this.id = id;
		this.fullName = fullName;
		this.children = children;
	}
	public ExcelImFieldModel(String id, String fullName) {
		this.id = id;
		this.fullName = fullName;
	}
}
