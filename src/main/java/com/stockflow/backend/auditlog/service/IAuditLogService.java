package com.stockflow.backend.auditlog.service;

import org.springframework.security.core.Authentication;

import com.stockflow.backend.auditlog.dto.AuditLogResponseDTO;
import com.stockflow.backend.auditlog.enumerate.AuditAction;

import jakarta.servlet.http.HttpServletRequest;

public interface IAuditLogService {
	
	public AuditLogResponseDTO saveAudit(Authentication auth,HttpServletRequest requests, AuditAction action, String entityName, Long entityId, Object RespNewValue, Object RespPrevValue, Integer httpStatus);
}
