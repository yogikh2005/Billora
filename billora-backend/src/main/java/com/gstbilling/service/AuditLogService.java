package com.gstbilling.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gstbilling.dao.AuditLogRepository;
import com.gstbilling.models.AuditLog;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;


    // =====================================================
    // SAVE AUDIT LOG
    // =====================================================

    public void logAction(
            String action,
            String details
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        String username = "SYSTEM";


        if (
                authentication != null
                && authentication.isAuthenticated()
        ) {

            username =
                    authentication.getName();
        }


        AuditLog log = new AuditLog();

        log.setAction(action);

        log.setPerformedBy(username);

        log.setDetails(details);

        log.setTimestamp(
                LocalDateTime.now()
        );


        // IMPORTANT
       // auditLogRepository.save(log);
    }


    // =====================================================
    // GET ALL LOGS
    // =====================================================

    public Page<AuditLog> getAllLogs(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );


        return auditLogRepository.findAll(
                pageable
        );
    }
}