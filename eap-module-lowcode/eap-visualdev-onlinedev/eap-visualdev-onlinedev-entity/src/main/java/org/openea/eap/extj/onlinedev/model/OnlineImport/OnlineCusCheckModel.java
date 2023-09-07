package org.openea.eap.extj.onlinedev.model.OnlineImport;

import lombok.Data;

import java.util.List;

@Data
public class OnlineCusCheckModel {
	private List<String> ableDepIds;
	private List<String> ableGroupIds;
	private List<String> ablePosIds;
	private List<String> ableRoleIds;
	private List<String> ableUserIds;
	/**
	 * 数据
	 */
	private List<String> dataList;
	/**
	 * 控件类型
	 */
	private String controlType;
}
