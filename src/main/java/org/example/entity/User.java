package org.example.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.example.enums.Role;

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
	private String middlename;

	@Column(name = "usr_login")
	private String login;

	@Column(name = "usr_password")
	private String password;

	@Column(name = "usr_role")
	@Enumerated(value = EnumType.STRING)
	private Role role;
}
