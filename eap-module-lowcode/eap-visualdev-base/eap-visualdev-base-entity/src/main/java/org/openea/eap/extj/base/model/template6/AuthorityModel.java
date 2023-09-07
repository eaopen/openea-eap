package org.openea.eap.extj.base.model.template6;

import lombok.Data;

/**
 * 权限控制字段
 */
@Data
public class AuthorityModel {
		/**
		 * 列表权限
		 */
		private Boolean useColumnPermission;
		/**
		 * 表单权限
		 */
		private Boolean useFormPermission;
		/**
		 * 按钮权限
		 */
		private Boolean useBtnPermission;
		/**
		 * 数据权限
		 */
		private Boolean useDataPermission;
}
