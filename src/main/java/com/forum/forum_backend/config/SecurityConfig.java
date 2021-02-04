package com.forum.forum_backend.config;

import com.forum.forum_backend.services.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserServiceImpl userService;
	private final JwtAuthenticationEntryPoint unauthorizedHandler;

	public SecurityConfig(UserServiceImpl userService, JwtAuthenticationEntryPoint unauthorizedHandler) {
		this.userService = userService;
		this.unauthorizedHandler = unauthorizedHandler;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/register", "/api/login")
				.permitAll()
				.antMatchers(HttpMethod.GET, "/api/threads", "/api/threads/*", "/api/forums", "/api/forums/*")
				.permitAll()
				.antMatchers(HttpMethod.POST, "/api/threads", "/api/threads/*", "/api/posts", "/api/posts/*")
				.hasAuthority("USER")
				.antMatchers(HttpMethod.POST, "/api/forums")
				.hasAnyAuthority("HEAD_MODERATOR", "ADMIN")
				.antMatchers(HttpMethod.POST, "/api/forums/*")
				.hasAnyAuthority("MODERATOR", "HEAD_MODERATOR", "ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/threads/*", "/api/posts/*")
				.hasAuthority("USER")
				.antMatchers(HttpMethod.PUT, "/api/forums/*")
				.hasAnyAuthority("MODERATOR", "HEAD_MODERATOR", "ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/threads/*", "/api/posts/*")
				.hasAuthority("USER")
				.antMatchers(HttpMethod.DELETE, "/api/forums/*")
				.hasAnyAuthority("MODERATOR", "HEAD_MODERATOR", "ADMIN")
				.and()
				.csrf().disable()
				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.cors();

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder () { return new BCryptPasswordEncoder();}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userService);
		authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());

		return authenticationProvider;
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {return new JwtAuthenticationFilter();}

	/*@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "UPGRADE"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}*/

}
