package org.openea.eap.extj.onlinedev.model.OnlineDevEnum;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * 在线开发缓存的key
 * 
 */
public enum CacheKeyEnum {
	/**
	 * 修改用户，创建用户，用户组件
	 */
	USER("_user","用户"),

	POS("_position","岗位"),

	ORG("_organization","组织"),

	AllORG("_organizationAll","组织多级"),

	ROLE("_role","角色"),

	GROUP("_group","分组");


	private final String name;
	private final String message;

	CacheKeyEnum(String name, String message) {
		this.name = name;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}


	/**
	 * 根据请求方式获取操作类型
	 *
	 * @return
	 */
	public static List<String> getAllCacheKey() {
		List<String> cacheKeyList = new ArrayList<>();
		for (CacheKeyEnum status : CacheKeyEnum.values()) {
				cacheKeyList.add(status.name);
			}
			return cacheKeyList;
		}
}
