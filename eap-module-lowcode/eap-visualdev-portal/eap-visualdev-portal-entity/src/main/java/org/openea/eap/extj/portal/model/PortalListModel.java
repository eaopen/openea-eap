package org.openea.eap.extj.portal.model;

import lombok.Data;

/**
 * 可视化列表模型

 */
@Data
public class PortalListModel {
	private String category;
	private Long creatorTime;
	private String creatorUser;
	private String enCode;
	private Integer enabledMark;
	private String fullName;
	private String id;
	private Integer type;
	private Long lastModifyTime;
	private String lastModifyUser;
	private Long sortCode;
}
