package com.stockflow.backend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jackson.SimpleGrantedAuthorityMixin;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JWTServiceImpl implements JWTService {
	
	public static final String SECRET = Base64.getEncoder()
	        .encodeToString("Alguna.Clave.Secreta.1234567890AB".getBytes());

	    public static final long EXPIRATION_DATE = 14000000L;
	    public static final String TOKEN_PREFIX = "Bearer ";
	    public static final String HEADER_STRING = "Authorization";

	    private SecretKey getSigningKey() {
	        byte[] keyBytes = Base64.getDecoder().decode(SECRET);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }

	    @Override
	    public String create(Authentication auth) throws IOException {
	        String username = ((UserDetails) auth.getPrincipal()).getUsername();
	        
	        // ✅ Filtrar solo SimpleGrantedAuthority, excluir FACTOR_PASSWORD y otros
	        Collection<? extends GrantedAuthority> roles = auth.getAuthorities()
	            .stream()
	            .filter(a -> a instanceof SimpleGrantedAuthority)
	            .collect(Collectors.toList());

	        return Jwts.builder()
	            .subject(username)
	            .claim("authorities", new ObjectMapper()
	                .registerModule(new JavaTimeModule())
	                .writeValueAsString(roles))
	            .issuedAt(new Date())
	            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE))
	            .signWith(getSigningKey())
	            .compact();
	    }

	    @Override
	    public boolean validate(String token) {
	        try {
	            getClaims(token);
	            return true;
	        } catch (JwtException | IllegalArgumentException e) {
	            return false;
	        }
	    }

	    @Override
	    public Claims getClaims(String token) {
	        return Jwts.parser()
	            .verifyWith(getSigningKey())
	            .build()
	            .parseSignedClaims(resolve(token))
	            .getPayload();
	    }

	    @Override
	    public String getUsername(String token) {
	        return getClaims(token).getSubject();
	    }

	    @Override
	    public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
	        Object roles = getClaims(token).get("authorities");
	        
	        List<Map<String, Object>> list = new ObjectMapper()
	            .registerModule(new JavaTimeModule())
	            .readValue(roles.toString(), new TypeReference<List<Map<String, Object>>>() {});
	        
	        return list.stream()
	            .filter(map -> map.containsKey("authority"))
	            .map(map -> new SimpleGrantedAuthority((String) map.get("authority")))
	            .collect(Collectors.toList());
	    }

	    @Override
	    public String resolve(String token) {
	        if (token != null && token.startsWith(TOKEN_PREFIX)) {
	            return token.replace(TOKEN_PREFIX, "");
	        }
	        return null;
	    }
}
