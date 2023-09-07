package org.openea.eap.extj.portal.model;
import lombok.Data;

import java.util.Date;

/**
 * 门户导入导出
 *
 */
@Data
public class PortalExportDataVo {

	private String id;

	private String description;

	private Long sortCode;

	private Integer enabledMark;

	private Date creatorTime;

	private String creatorUser;

	private Date lastModifyTime;

	private String lastModifyUser;

	private Integer deleteMark;

	private Date deleteTime;

	private String deleteUserId;

	private String fullName;

	private String enCode;

	private String category;

	private String formData;

	private Integer type;

	private String customUrl;

	private Integer linkType;

	private String modelType;

	private Integer enabledLock;
}
