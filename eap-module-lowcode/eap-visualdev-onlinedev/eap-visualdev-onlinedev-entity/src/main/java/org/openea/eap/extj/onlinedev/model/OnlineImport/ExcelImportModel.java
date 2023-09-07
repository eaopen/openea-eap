package org.openea.eap.extj.onlinedev.model.OnlineImport;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 * 在线开发导入数据结果集

 */
@Data
public class ExcelImportModel {
	/**
	 * 导入成功条数
	 */
	private int snum;
	/**
	 * 导入失败条数
	 */
	private int fnum;
	/**
	 * 导入结果状态(0,成功  1，失败)
	 */
	private int resultType;

	/**
	 * 失败结果
	 */
	private List<Map<String, Object>> failResult;
}
