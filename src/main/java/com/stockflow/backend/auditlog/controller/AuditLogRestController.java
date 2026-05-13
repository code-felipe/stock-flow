package com.stockflow.backend.auditlog.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockflow.backend.auditlog.dto.summary.AuditLogResponseDTO;
import com.stockflow.backend.auditlog.service.IAuditLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/audit")
@Tag(name = "AuditLog", description = "Endpoints for audit logs")
public class AuditLogRestController {

	
	@Autowired
	private IAuditLogService auditService;
	
	@GetMapping
	@Operation(
            summary = "Get audits",
            description = "Paginated audit list, sorted by performedAt (desc)."
    )
	public ResponseEntity<Map<String, Object>> list(
			@Parameter(description = "Zero-based page index", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size", example = "10")
			@RequestParam(defaultValue = "10") int size
			){
		
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "performedAt"));
		Page<AuditLogResponseDTO> result = auditService.findAll(pageable);
		
		
		Map<String, Object> body = new HashMap<>();
		body.put("messsage", "All logs were successfully fetched");
        body.put("audits", result);
        
        return ResponseEntity.ok(body);
		
	}
}
