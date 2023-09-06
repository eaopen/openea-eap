package org.openea.eap.extj.model.visualJson.superQuery;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 高级查询
 *
 * 
 */
@Data
public class SuperQueryJsonModel {
	@JSONField( name = "matchLogic")
	private String matchLogic;
	@JSONField( name = "conditionJson")
	private String conditionJson;
}
