package com.stockflow.backend.auditlog.service;



import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Pageable;

import com.stockflow.backend.auditlog.dto.summary.AuditLogResponseDTO;
import com.stockflow.backend.auditlog.enumerate.AuditAction;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuditLogService {
	
	public Page<AuditLogResponseDTO> findAll(Pageable page);
	
	public AuditLogResponseDTO saveAudit(Authentication auth,HttpServletRequest requests, AuditAction action, String entityName, Long entityId, Object RespNewValue, Object RespPrevValue, Integer httpStatus);
	
	void saveFailedAudit(Authentication auth, HttpServletRequest request, 
	        int httpStatus, String errorMessage);
}
