package org.openea.eap.extj.onlinedev.model.OnlineDevEnum;

/**
 * 控件多选字符
 *
 *
 */

public enum MultipleControlEnum {
	/**
	 * 数组
	 */
	MULTIPLE_JSON_ONE("[",1),
	/**
	 * 二维数组
	 */
	MULTIPLE_JSON_TWO("[[",2),
	/**
	 * 普通字符
	 */
	MULTIPLE_JSON_THREE("",3);


	MultipleControlEnum(String multipleChar, int dataType) {
		MultipleChar = multipleChar;
		DataType = dataType;
	}

	public String getMultipleChar() {
		return MultipleChar;
	}

	public int getDataType() {
		return DataType;
	}

	private String MultipleChar;
	private int DataType;

}
