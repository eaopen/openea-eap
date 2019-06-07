package org.openea.eap.cloud.oauth.enums;

public enum UserStatus {

	ACTIVE("Active", 1),
	INACTIVE("Inactive", 2);

	private String label;
	private Integer value;

	private UserStatus(String label, Integer value) {
		this.label = label;
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public static UserStatus getEnum(Integer value) {
		for (UserStatus item : UserStatus.values()) {
			if (item.getValue().equals(value)) {
				return item;
			}
		}
		return null;
	}
}
