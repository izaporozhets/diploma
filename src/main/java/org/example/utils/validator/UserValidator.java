package org.example.utils.validator;

import java.util.Arrays;
import org.example.payload.request.RegistrationRequest;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Component
public class UserValidator {

	private final UserRepository userRepository;

	public UserValidator(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public boolean validateCreateRequest(Model model, RegistrationRequest request, BindingResult bindingResult){
		model.addAttribute("usernameTaken", userRepository.findByUsername(request.getUsername()).isPresent());
		bindingResult.getFieldErrors("temporaryPassword").stream().filter(fieldError -> fieldError.getDefaultMessage() != null)
			.flatMap(fieldError -> Arrays.stream(fieldError.getDefaultMessage().split(",")))
			.forEach(message -> {
				switch (message) {
					case "Password must be 8 or more characters in length.":
						model.addAttribute("mustBeMore", true);
						break;
					case "Password must contain 1 or more uppercase characters.":
						model.addAttribute("mustContainUpper", true);
						break;
					case "Password must contain 1 or more lowercase characters.":
						model.addAttribute("mustContainLower", true);
						break;
					case "Password must contain 1 or more digit characters.":
						model.addAttribute("mustContainDigit", true);
						break;
					case "Password must contain 1 or more special characters.":
						model.addAttribute("mustContainSpecial", true);
						break;
				}
			});
		return userRepository.findByUsername(request.getUsername()).isPresent() || bindingResult.hasErrors();
	}

	public boolean validateConfirmRequest(Model model, BindingResult bindingResult){
		bindingResult.getFieldErrors("password").stream().filter(fieldError -> fieldError.getDefaultMessage() != null)
			.flatMap(fieldError -> Arrays.stream(fieldError.getDefaultMessage().split(",")))
			.forEach(message -> {
				switch (message) {
					case "Password must be 8 or more characters in length.":
						model.addAttribute("mustBeMore", true);
						break;
					case "Password must contain 1 or more uppercase characters.":
						model.addAttribute("mustContainUpper", true);
						break;
					case "Password must contain 1 or more lowercase characters.":
						model.addAttribute("mustContainLower", true);
						break;
					case "Password must contain 1 or more digit characters.":
						model.addAttribute("mustContainDigit", true);
						break;
					case "Password must contain 1 or more special characters.":
						model.addAttribute("mustContainSpecial", true);
						break;
				}
			});
		return bindingResult.hasErrors();
	}
}
