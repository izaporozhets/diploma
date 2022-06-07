package org.example.controller;

import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import org.example.payload.dto.SearchDto;
import org.example.payload.request.LoginRequest;
import org.example.payload.request.RegistrationRequest;
import org.example.service.UserService;
import org.example.utils.validator.UserValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

	private final UserService userService;
	private final UserValidator userValidator;

	public UserController(UserService userService, UserValidator userValidator) {
		this.userService = userService;
		this.userValidator = userValidator;
	}

	@PostMapping("/app/v1/users/register")
	public String registerUser(Model model, @Valid @ModelAttribute RegistrationRequest request, BindingResult bindingResult){
		if (userValidator.validateCreateRequest(model, request, bindingResult)){
			model.addAttribute("registrationRequest", request);
			model.addAttribute("roleNames", List.of("Користувач", "Адміністратор"));
			return "users/user-create";
		}
		userService.registrationProcess(request);
		return "redirect:/app/v1/users";
	}

	@GetMapping("/app/v1/users")
	public String getUsersPage(Model model, @RequestParam(value = "search", required = false) String searchValue){
		userService.fillUsersModel(model, searchValue);
		return "users/users";
	}

	@GetMapping("/app/v1/users/create")
	public String getUserCreatePage(Model model, @ModelAttribute RegistrationRequest request){
		model.addAttribute("registrationRequest", request);
		model.addAttribute("roleNames", List.of("Користувач", "Адміністратор"));
		return "users/user-create";
	}

	@GetMapping("/app/v1/login")
	public String getLoginPage(Model model){
		model.addAttribute("request", new LoginRequest());
		model.addAttribute("isPasswordConfirmed", true);
		return "login";
	}

	@GetMapping("/app/v1/auth")
	public String index(Model model){
		model.addAttribute("searchDto", new SearchDto());
		return "index";
	}

	@PostMapping("/app/v1/users/delete")
	public String deleteUser(@RequestParam(value = "userId") Integer userId){
		userService.deleteUser(userId);
		return "redirect:/app/v1/users";
	}

	@PostMapping("/app/v1/users/confirm")
	public String confirmPassword(Model model,@ModelAttribute @Valid LoginRequest request, BindingResult bindingResult){
		boolean arePasswordsSame = userService.confirmPassword(request.getUsername(), request.getPassword() , request.getConfirmedPassword());
		if(!arePasswordsSame || userValidator.validateConfirmRequest(model, bindingResult)){
			model.addAttribute("request", new LoginRequest().setUsername(request.getUsername()));
			model.addAttribute("arePasswordsSame", !arePasswordsSame);
			model.addAttribute("isPasswordConfirmed", false);
			return "login";
		}
		return "redirect:/app/v1/home";
	}

	@GetMapping("/app/v1/home")
	public String home(Authentication authentication, Model model){
		String username = authentication.getName();
		Boolean isPasswordConfirmed = userService.loginAction(username);
		if(!isPasswordConfirmed) {
			model.addAttribute("request", new LoginRequest());
			model.addAttribute("usrnm", username);
			model.addAttribute("isPasswordConfirmed", false);
			return "login";
		}

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for (GrantedAuthority authority : authorities) {
			if (authority.getAuthority().contains("user")) {
				return "redirect:/app/v1/groups";
			}
			if (authority.getAuthority().contains("admin")) {
				return "redirect:/app/v1/auth";
			}
		}

		return "index";
	}

}
