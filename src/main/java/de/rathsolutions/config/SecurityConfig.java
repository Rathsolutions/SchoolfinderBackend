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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String CSRF_TOKEN = "X-XSRF-TOKEN";

	@Autowired
	private AuthenticationEntryPoint authEntryPoint;

	@Autowired
	private CookieCsrfTokenRepository csrfRepo;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		//@formatter:off
		http
			.authorizeHttpRequests(req->req.requestMatchers(
				"/api/v1/finder/search/**", 
				"/api/v1/schools/search/**", 
				"/api/v1/schools",
				"/api/v1/criterias/search/getAllAvailableCriterias/**", 
				"/api/v1/*/search/findAll",
				"/api/v1/persons/search/**",
				"/api/v1/schoolType/search/**", 
				"/api/v1/project/*")
				.permitAll()
				.requestMatchers("/actuator/**")
					.authenticated()
				.requestMatchers("/**")
					.authenticated())
					.httpBasic(Customizer.withDefaults())
				// .csrf(configurer->configurer.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
				.csrf(configurer->configurer.csrfTokenRepository(csrfRepo).csrfTokenRequestHandler(requestHandler))
//				.and()
				.cors(Customizer.withDefaults());
		http.headers(headers->headers.frameOptions(fo->fo.sameOrigin()));
		http.exceptionHandling(exHandler->exHandler.authenticationEntryPoint(authEntryPoint));

		//@formatter:off
		return http.build();
	}
    
	// @Bean
	// AuthenticationProvider authenticationProvider() {
	// 	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
	// 	authenticationProvider.setUserDetailsService(this.userDetailsService);
	// 	authenticationProvider.setPasswordEncoder(this.passwordEncoder);
	// 	return authenticationProvider;
	// }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
	final CorsConfiguration configuration = new CorsConfiguration();
	configuration.setAllowedOrigins(
		Arrays.asList("https://schoolfindernew.rathsolutions.de", "http://localhost:4200", "https://schoolfinder.rathsolutions.de"));
	configuration.setAllowedHeaders(Collections.singletonList("*"));
	configuration.addExposedHeader("Authorization");
	configuration.setAllowCredentials(true);
	configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
	final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source.registerCorsConfiguration("/**", configuration);
	return source;
    }


}
