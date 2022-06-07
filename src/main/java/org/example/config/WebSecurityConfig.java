package org.example.config;

import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.List;
import okhttp3.Request;
import org.example.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${app.id}")       private String APP_ID;
	@Value("${app.scopes}")   private String scopes;

	@Bean
	public GraphServiceClient<Request> graphServiceClient(){

		final DeviceCodeCredential credential = new DeviceCodeCredentialBuilder()
			.clientId(APP_ID)
			.challengeConsumer(challenge -> System.out.println(challenge.getMessage()))
			.build();

		TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(List.of(scopes.split(",")), credential);
		// Create default logger to only log errors
		DefaultLogger logger = new DefaultLogger();
		logger.setLoggingLevel(LoggerLevel.ERROR);

		// Build a Graph client
		return GraphServiceClient.builder()
			.authenticationProvider(authProvider)
			.logger(logger)
			.buildClient();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(){
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setPasswordEncoder(passwordEncoder());
		authProvider.setUserDetailsService(userDetailsService());
		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth){
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		String[] staticResources  =  {
			"/static/images/**",
			"/static/css/**"
		};

		http.csrf().disable()
			.authorizeRequests()
			.antMatchers("/app/v1/users/confirm").permitAll().anyRequest().authenticated()
			.and().formLogin().loginPage("/app/v1/login")
			.permitAll().defaultSuccessUrl("/app/v1/home")
			.and()
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/app/v1/logout", "POST"))
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.deleteCookies("JSESSIONID")
			.logoutSuccessUrl("/app/v1/login");

	}
}
