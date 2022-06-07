package org.example.service.impl;

import org.example.entity.User;
import org.example.enums.ApplicationError;
import org.example.exception.ApplicationException;
import org.example.repository.UserRepository;
import org.example.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new ApplicationException(ApplicationError.USER_DOES_NOT_EXIST));
		return MyUserDetails.fromUser(user);
	}
}
