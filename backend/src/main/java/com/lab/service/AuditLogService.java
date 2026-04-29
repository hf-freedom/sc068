package com.lab.service;

import com.lab.entity.AuditLog;
import com.lab.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String operationType, String relatedId, String userId, String detail, String ip) {
        AuditLog log = new AuditLog();
        log.setOperationType(operationType);
        log.setRelatedId(relatedId);
        log.setUserId(userId);
        log.setOperationTime(LocalDateTime.now());
        log.setDetail(detail);
        log.setIp(ip);
        auditLogRepository.save(log);
    }

    public void log(String operationType, String relatedId, String userId, String detail) {
        log(operationType, relatedId, userId, detail, "127.0.0.1");
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
