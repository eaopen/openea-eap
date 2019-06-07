package org.openea.eap.cloud.oauth.enums;

public enum RoleStatus {
	ACTIVE("Active", "A"),
	INACTIVE("Inactive", "I"),
	DELETED("Deleted", "D");

	private String label;
	private String value;

	private RoleStatus(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public static RoleStatus getEnum(String value) {
		for (RoleStatus item : RoleStatus.values()) {
			if (item.getValue().equalsIgnoreCase(value)) {
				return item;
			}
		}
		return null;
	}

}
