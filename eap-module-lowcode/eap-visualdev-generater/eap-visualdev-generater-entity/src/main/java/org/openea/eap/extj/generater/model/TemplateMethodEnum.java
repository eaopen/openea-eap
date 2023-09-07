package org.openea.eap.extj.generater.model;
/**
 *
 * 模板路径
 *
 */
public enum TemplateMethodEnum {
	T2("TemplateCode2"),
	T3("TemplateCode3"),
	T4("TemplateCode4"),
	T5("TemplateCode5");

	TemplateMethodEnum(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	private String method;
}
