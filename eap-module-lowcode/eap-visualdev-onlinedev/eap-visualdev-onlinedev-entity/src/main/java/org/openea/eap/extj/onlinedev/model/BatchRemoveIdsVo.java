package org.openea.eap.extj.onlinedev.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批量删除id集合
 *
 */
@Data
@Schema(description = "批量处理参数")
public class BatchRemoveIdsVo {
	@Schema(description = "批量处理数据id")
		private String[] ids;
}
