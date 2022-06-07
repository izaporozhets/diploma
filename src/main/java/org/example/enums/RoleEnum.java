package org.example.enums;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum RoleEnum {
	UNKNOWN(Set.of(Permission.UNKNOWN_READ), "UNKNOWN"),
	ADMIN(Set.of(Permission.ADMINISTRATOR_READ, Permission.ADMINISTRATOR_WRITE), "Адміністратор"),
	USER(Set.of(Permission.USER_READ, Permission.USER_WRITE), "Користувач");

	private final Set<Permission> permissions;
	private final String roleName;

	RoleEnum(Set<Permission> permissions, String roleName) {
		this.permissions = permissions;
		this.roleName = roleName;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public static RoleEnum getRoleEnumByRoleName(String roleName){
		Map<String, RoleEnum> map = Map.of(UNKNOWN.roleName, UNKNOWN, ADMIN.roleName, ADMIN, USER.roleName, USER);
		return map.get(roleName);
	}

	public Set<SimpleGrantedAuthority> getAuthorities() {
		return getPermissions().stream()
			.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
			.collect(Collectors.toSet());
	}
}
