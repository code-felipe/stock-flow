package com.stockflow.backend;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockflow.backend.auth.filter.JWTAuthenticationFilter;
import com.stockflow.backend.auth.filter.JWTAuthorizationFilter;
import com.stockflow.backend.auth.service.JWTService;
import com.stockflow.backend.user.service.JpaUserDetailsService;

@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig {
	
	@Autowired
	private JpaUserDetailsService userDetailsService;
	
	@Autowired
    private AuthenticationConfiguration authenticationConfiguration;
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void userDetailsService(AuthenticationManagerBuilder build) throws Exception {
       build.userDetailsService(userDetailsService)
       .passwordEncoder(passwordEncoder);
    }
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
                .requestMatchers("/api/login").permitAll()
//                .anyRequest().permitAll()//without auth
                .requestMatchers("/api/admin/**").authenticated()//with auth
                .anyRequest().authenticated()//with authw
            )
        	.exceptionHandling(ex -> ex
            	.accessDeniedHandler(accessDeniedHandler())//Sends an exception for non-authorizate user
            		)
            .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtService))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtService))
            .csrf(config -> config.disable())
            .sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
 
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Access denied: You have not permissions to access this source");
            body.put("error", accessDeniedException.getMessage());
            
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        };
    }

}
