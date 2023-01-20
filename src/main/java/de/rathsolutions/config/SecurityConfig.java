/*-
 * #%L
 * SchuglemapsBackend
 * %%
 * Copyright (C) 2020 Rathsolutions. <info@rathsolutions.de>
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package de.rathsolutions.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String CSRF_TOKEN = "X-XSRF-TOKEN";

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//@formatter:off
		http.authorizeHttpRequests(req->req.requestMatchers(
						"/api/v1/cities/search/**", 
						"/api/v1/schools/search/**", 
						"/api/v1/schools",
						"/api/v1/criterias/search/getAllAvailableCriterias/**")
				.permitAll()
				.requestMatchers("/actuator/**")
					.authenticated()
				.requestMatchers("/**")
					.authenticated())
				.httpBasic()
				.and()
				.csrf()
					.disable()
				.cors()
				.and()
					.authenticationProvider(this.authenticationProvider());
		http.headers().frameOptions().sameOrigin();
		//@formatter:off
		return http.build();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.userDetailsService);
		authenticationProvider.setPasswordEncoder(this.passwordEncoder);
		return authenticationProvider;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();

		configuration
				.setAllowedOrigins(Arrays.asList("https://schoolfinder.rathsolutions.de", "http://localhost:4200"));
		configuration.setAllowedHeaders(Collections.singletonList("*"));
		configuration.addExposedHeader("Authorization");
		configuration.setAllowCredentials(true);
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
