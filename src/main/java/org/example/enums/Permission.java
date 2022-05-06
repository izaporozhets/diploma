package org.example.enums;

public enum Permission {
	UNKNOWN_READ("unknown:read"),
	ADMINISTRATOR_READ("admin:read"),
	ADMINISTRATOR_WRITE("admin:write"),
	USER_READ("user:read"),
	USER_WRITE("user:write");

	private final String permission;

	Permission(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}
}
