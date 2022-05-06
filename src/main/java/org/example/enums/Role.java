package org.example.enums;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
	UNKNOWN(Set.of(Permission.UNKNOWN_READ)),
	ADMIN(Set.of(Permission.ADMINISTRATOR_READ, Permission.ADMINISTRATOR_WRITE)),
	USER(Set.of(Permission.USER_READ, Permission.USER_WRITE));

	private final Set<Permission> permissions;

	Role(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public Set<SimpleGrantedAuthority> getAuthorities() {
		return getPermissions().stream()
			.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
			.collect(Collectors.toSet());
	}
}
