package org.openea.eap.extj.base.model.online;

import lombok.Data;

import java.util.List;

@Data
public class PerColModels {
	/**
	 * 数据权限
	 */
	private List<AuthFlieds> dataPermission;

	/**
	 * 表单权限
	 */
	private List<AuthFlieds> formPermission;

	/**
	 * 列表权限
	 */
	private List<AuthFlieds> listPermission;

	/**
	 * 按钮权限
	 */
	private List<AuthFlieds> buttonPermission;

	/**
	 * 数据权限方案
	 */
	private List<AuthFlieds> dataPermissionScheme;

}
