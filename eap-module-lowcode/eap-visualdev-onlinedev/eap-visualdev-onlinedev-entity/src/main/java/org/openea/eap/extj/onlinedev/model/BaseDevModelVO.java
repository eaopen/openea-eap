package org.openea.eap.extj.onlinedev.model;

import lombok.Data;

/**
 * 功能设计导入导出模型
 *
 */
@Data
public class BaseDevModelVO {

	private String id;

	private String description;

	private String sortCode;

	private String enabledMark;

	private String creatorTime;

	private String creatorUser;

	private String lastModifyTime;

	private String lastModifyUser;

	private String deleteMark;

	private String deleteTime;

	private String deleteUserId;

	private String fullName;

	private String enCode;

	private String state;

	private String type;

	private String tables;

	private String category;

	private String formData;

	private String columnData;

	private String dbLinkId;

	private String webType;

	private String modelType;

	private String enabledFlow;
}
