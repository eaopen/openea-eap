package org.openea.eap.extj.annotation;

import java.lang.annotation.*;

/**
 * 控件属性
 *
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JnpfField {

	String vModel() default "";

	String label() default "";

	/**
	 * 是否多选
	 */
	boolean multiple() default false;

	String jnpfKey() default "";

	/**
	 * 显示层级
	 */
	String showLevel() default "";
	/**
	 * 省市区显示层级
	 */
	String level() default "0";

	/**
	 * 单据规则
	 */
	String rule() default "";

	String activeTxt() default "开";

	String inactiveTxt() default "关";

	int min() default -1;

	int max() default -1;

	/**
	 *
	 * 是否唯一
	 */
	boolean unique() default false;


	boolean isUpdate() default false;

	/**
	 * 单行输入正则
	 */
	String regex() default "";


	/**
	 * 表名
	 */
	String relationTable() default "";

	String tableName() default "";

	/**
	 * 时间
	 */
	String format() default "";

	/**
	 * 数据接口
	 */
	String dataType() default "";

	String dataLabel() default "fullName";

	String dataValue() default "id";

	String dataChildren() default "children";

	String propsUrl() default "";

	String dictionaryType() default "";

	String options() default "";

	String ableDepIds() default "[]";

	String ablePosIds() default "[]";

	String ableUserIds() default "[]";

	String ableRoleIds() default "[]";

	String ableGroupIds() default "[]";

	String ableIds() default "[]";

	String selectType() default "";
}
