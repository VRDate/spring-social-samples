package org.springframework.social.showcase.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialAuthenticationProvider;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.UserIdExtractor;
import org.springframework.social.showcase.security.AuthenticationUserIdExtractor;
import org.springframework.social.showcase.security.FacebookAuthenticationService;
import org.springframework.social.showcase.security.SimpleSocialUsersDetailService;

@Configuration
public class SocialSecurityConfig {

	@Inject
	private Environment environment;

	@Inject
	private UsersConnectionRepository usersConnectionRepository;
	
	@Bean 
	public SocialAuthenticationFilter socialAuthenticationFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices) {
		SocialAuthenticationFilter socialAuthenticationFilter = new SocialAuthenticationFilter(authenticationManager, userIdExtractor(), usersConnectionRepository, authenticationServiceLocator());
		socialAuthenticationFilter.setFilterProcessesUrl("/auth");
		socialAuthenticationFilter.setSignupUrl("/spring-social-showcase/signup"); // TODO: Fix filter to handle in-app paths
		socialAuthenticationFilter.setRememberMeServices(rememberMeServices);
		return socialAuthenticationFilter;
	}

	@Bean
	public AuthenticationProvider socialAuthenticationProvider(UserDetailsService userDetailsService) {
		return new SocialAuthenticationProvider(usersConnectionRepository, socialUsersDetailsService(userDetailsService));
	}
	
	@Bean
	public SocialUserDetailsService socialUsersDetailsService(UserDetailsService userDetailsService) {
		return new SimpleSocialUsersDetailService(userDetailsService);
	}
	
	@Bean
	public UserIdExtractor userIdExtractor() {
		return new AuthenticationUserIdExtractor();
	}
	
	@Bean
	public SocialAuthenticationServiceRegistry authenticationServiceLocator() {
		SocialAuthenticationServiceRegistry authenticationServiceRegistry = new SocialAuthenticationServiceRegistry();
		FacebookAuthenticationService facebookAuthenticationService = new FacebookAuthenticationService(environment.getProperty("facebook.clientId"), environment.getProperty("facebook.clientSecret"));
		facebookAuthenticationService.setScope("user_about_me");
		authenticationServiceRegistry.addAuthenticationService(facebookAuthenticationService);
		return authenticationServiceRegistry;
	}
	
}

