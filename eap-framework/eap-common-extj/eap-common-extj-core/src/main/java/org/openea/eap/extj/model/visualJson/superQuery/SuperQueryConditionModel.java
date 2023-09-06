package org.openea.eap.extj.model.visualJson.superQuery;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 高级查询（代码生成器）
 *
 * 
 */
@Data
@AllArgsConstructor
public class SuperQueryConditionModel<T> {
	private QueryWrapper<T> obj;
	private List<ConditionJsonModel> conditionList;
	private String matchLogic;
	private String tableName;
}
