package com.stockflow.backend.user.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockflow.backend.user.domain.User;
import com.stockflow.backend.user.repository.IUserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {
	
	@Autowired
	private IUserRepository userRepo;
	
	private Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> {
			        logger.error("Login Error: User does not exists '" + username + "' in the system!");
			        return new UsernameNotFoundException("Username: " + username + " does not exists in the system!");
			    });;
			 // logs temporales
			    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			    logger.info("Password match result: " + encoder.matches("123456", user.getPassword()));
			    logger.info("Nuevo hash: " + encoder.encode("123456")); // ✅ agrega esto
			    logger.info("Password hash from DB: " + user.getPassword());
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		user.getRoles().stream()
			.forEach(r -> {
				logger.info("Role: ".concat(r.getAuthority()));
				authorities.add(new SimpleGrantedAuthority(r.getAuthority()));
			});
		
		if(authorities.isEmpty()) {
			logger.error("Login Error: '" + username + "' Does not have asigned roles!");
        	throw new UsernameNotFoundException("Login Error: user '" + username + "' does not have asigned roles!");
		}
		logger.info("Password hash from DB: " + user.getPassword());
		
		return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),user.getEnabled(),true,true,true,authorities);
	}

}
