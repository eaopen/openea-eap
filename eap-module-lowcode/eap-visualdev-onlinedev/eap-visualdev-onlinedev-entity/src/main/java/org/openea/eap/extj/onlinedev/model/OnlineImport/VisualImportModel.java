package org.openea.eap.extj.onlinedev.model.OnlineImport;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 导入失败的数据
 *
 */
@Data
@Schema(description = "导入参数")
public class VisualImportModel {
	@Schema(description = "数据数组")
	private List<Map<String, Object>> list;
}
