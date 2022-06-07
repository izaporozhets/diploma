package org.example.service;

import org.example.payload.request.RegistrationRequest;
import org.springframework.ui.Model;

public interface UserService {
	void registrationProcess(RegistrationRequest request);

	boolean isUsernameTaken(String username);

	void fillUsersModel(Model model, String searchValue);

	Boolean loginAction(String name);

	boolean confirmPassword(String username, String password, String confirmPassword);

	void deleteUser(Integer userId);
}
