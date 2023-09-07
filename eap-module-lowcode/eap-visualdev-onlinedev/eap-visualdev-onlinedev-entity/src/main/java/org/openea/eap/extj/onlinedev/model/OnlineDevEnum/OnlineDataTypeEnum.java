package org.openea.eap.extj.onlinedev.model.OnlineDevEnum;



/**
 *
 * 数据接口类型
 * 
 */

public enum OnlineDataTypeEnum {
	/**
	 * 静态数据
	 */
	STATIC("static","静态数据"),
	/**
	 * 数据字典
	 */
	DICTIONARY("dictionary","数据字典"),
	/**
	 * 远端数据
	 */
	DYNAMIC("dynamic","远端数据"),
	/**
	 * 二维码 条形码类型
	 */
	STATIC_CODE("static","固定值"),

	RELATION("relation","关联组件")
	;


	private final String type;
	private final String message;


	OnlineDataTypeEnum(String type, String message) {
		this.type = type;
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

}
