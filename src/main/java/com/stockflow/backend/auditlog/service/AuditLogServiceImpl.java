package com.stockflow.backend.auditlog.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockflow.backend.auditlog.domain.AuditLog;
import com.stockflow.backend.auditlog.dto.AuditLogResponseDTO;
import com.stockflow.backend.auditlog.enumerate.AuditAction;
import com.stockflow.backend.auditlog.repository.IAuditLogRepository;
import com.stockflow.backend.user.domain.User;
import com.stockflow.backend.user.repository.IUserRepository;
import com.stockflow.backend.utils.mapper.Mapper;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditLogServiceImpl  implements IAuditLogService{

	@Autowired
	private IAuditLogRepository auditRepo;
	
	@Autowired
	private IUserRepository userRepo;
	
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Transactional(propagation = Propagation.REQUIRES_NEW) // transacción independiente
	@Override
	public AuditLogResponseDTO saveAudit(Authentication auth, HttpServletRequest requests, AuditAction action,
			String entityName, Long entityId, Object respNewValue, Object respPrevValue, Integer httpStatus) {
		// TODO Auto-generated method stub
		String username = auth.getName();
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> {			 
			        return new UsernameNotFoundException("Username: " + username + " does not exists in the system!");
			    });;

		
		try {
			return Mapper.createAuditLog(auditRepo.save(AuditLog.builder() 
					.performBy(username)
					.userId(user != null ? user.getId() : null)
					.entityName(entityName)
					.entityId(entityId)
					.action(action)
					.httMethod(requests.getMethod())
					.endpoint(requests.getRequestURI())
					.newValue(objectMapper.writeValueAsString(respNewValue))
					.previousValue(objectMapper.writeValueAsString(respPrevValue))
					.performedAt(Instant.now())
					.httpStatus(httpStatus)
					.succes(true)
					.build()));
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("AUDIT ERROR: " + e.getMessage());
	        e.printStackTrace();
	        return null;
		}
	}

}
