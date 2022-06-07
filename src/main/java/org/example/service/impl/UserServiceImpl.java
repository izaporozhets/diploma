package org.example.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.User;
import org.example.enums.RoleEnum;
import org.example.exception.ApplicationException;
import org.example.payload.data.UserData;
import org.example.payload.request.RegistrationRequest;
import org.example.repository.UserRepository;
import org.example.service.ConverterService;
import org.example.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ConverterService converterService;

	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, ConverterService converterService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.converterService = converterService;
	}

	@Override
	public void registrationProcess(RegistrationRequest request) {
		User user = new User();
		user.setName(request.getName());
		user.setSurname(request.getSurname());
		user.setPatronymic(request.getPatronymic());
		user.setUsername(request.getUsername());
		user.setIsPasswordConfirmed(false);
		user.setRoleEnum(RoleEnum.getRoleEnumByRoleName(request.getRoleName()));
		user.setPassword(passwordEncoder.encode(request.getTemporaryPassword()));
		userRepository.save(user);
	}

	@Override
	public boolean isUsernameTaken(String username) {
		return userRepository.findByUsername(username).isPresent();
	}

	@Override
	public void fillUsersModel(Model model, String searchValue) {
		List<UserData> userDataList = Objects.isNull(searchValue) ?
			userRepository.findAll().stream().map(converterService::toUserData).collect(Collectors.toList()) :
			userRepository.findAllBySearchValue(searchValue).stream().map(converterService::toUserData).collect(Collectors.toList());
		model.addAttribute("users", userDataList);
	}

	@Override
	public Boolean loginAction(String name) {
		User user = userRepository.findByUsername(name).orElseThrow(() -> new ApplicationException(2, "User was not found"));
		user.setLastLogin(LocalDateTime.now());
		return userRepository.save(user).getIsPasswordConfirmed();
	}

	@Override
	public boolean confirmPassword(String username, String confirmPassword, String password) {
		if (!password.equals(confirmPassword)) {
			return false;
		}
		User user = userRepository.findByUsername(username).orElseThrow(() -> new ApplicationException(2, "User was not found"));
		user.setIsPasswordConfirmed(true);
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
		return true;
	}

	@Override
	public void deleteUser(Integer userId) {
		userRepository.deleteById(userId);
	}
}
