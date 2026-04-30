package com.stockflow.backend.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockflow.backend.auth.handler.LoginRequest;
import com.stockflow.backend.auth.service.JWTService;
import com.stockflow.backend.auth.service.JWTServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.FilterChain;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private AuthenticationManager authenticationManager;
	private JWTService jwtService;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
	    this.authenticationManager = authenticationManager;
	    this.setRequiresAuthenticationRequestMatcher(
	        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/login")
	    );
	    this.jwtService = jwtService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
	        throws AuthenticationException {

	    String username = obtainUsername(request);
	    String password = obtainPassword(request);

	    if (username != null && password != null) {
	        logger.info("Username desde request parameter (form-data): " + username);
	        logger.info("Password desde request parameter (form-data): " + password);

	    } else {
	        try {
	            LoginRequest credentials = new ObjectMapper()
	                    .readValue(request.getInputStream(), LoginRequest.class);

	            username = credentials.getUsername();
	            password = credentials.getPassword();

	            logger.info("Username desde request InputStream (raw): " + username);
	            logger.info("Password desde request InputStream (raw): " + password);

	        } catch (JsonParseException e) {
	            e.printStackTrace();
	        } catch (JsonMappingException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    if (username == null || password == null) {
	        throw new AuthenticationServiceException("Invalid or missing credentials in the request.");
	    }

	    username = username.trim();

	    UsernamePasswordAuthenticationToken authToken = 
	            new UsernamePasswordAuthenticationToken(username, password);

	    return authenticationManager.authenticate(authToken);
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
	        Authentication authResult) throws IOException, ServletException {
		
	    String token = jwtService.create(authResult);
	    
	    response.addHeader(JWTServiceImpl.HEADER_STRING, JWTServiceImpl.TOKEN_PREFIX + token);
	    
	    //Setear status y contentType ANTES de escribir
	    response.setStatus(200);
	    response.setContentType("application/json");

	    Map<String, Object> body = new HashMap<String, Object>();
	    body.put("token", token);
	    body.put("user", (User) authResult.getPrincipal());
	    body.put("mensaje", String.format("Hola %s, has iniciado sesión con éxito!", 
	            ((User) authResult.getPrincipal()).getUsername()));

	    response.getWriter().write(new ObjectMapper().writeValueAsString(body));//Converts any object in java to Json
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("mensaje", "Error de autenticación: username o password incorrecto!");
		body.put("error", failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}
}
