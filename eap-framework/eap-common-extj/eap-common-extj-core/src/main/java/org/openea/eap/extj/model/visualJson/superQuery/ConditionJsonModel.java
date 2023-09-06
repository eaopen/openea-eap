package org.openea.eap.extj.model.visualJson.superQuery;

import lombok.Data;

/**
 * 高级查询
 *
 *
 */
@Data
public class ConditionJsonModel {
	private String field;
	private String fieldValue;
	private String symbol;
	private String tableName;
	private String jnpfKey;
	private String attr;
}
