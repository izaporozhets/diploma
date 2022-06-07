package org.example.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.example.enums.RoleEnum;

@Data
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usr_id")
	private Integer id;

	@Column(name = "usr_name")
	private String name;

	@Column(name = "usr_surname")
	private String surname;

	@Column(name = "usr_patronymic")
	private String patronymic;

	@Column(name = "usr_username")
	private String username;

	@Column(name = "usr_password")
	private String password;

	@Column(name = "usr_password_confirmed")
	private Boolean isPasswordConfirmed;

	@Column(name = "usr_last_login")
	private LocalDateTime lastLogin;

	@Column(name = "usr_role")
	@Enumerated(value = EnumType.STRING)
	private RoleEnum roleEnum;
}
