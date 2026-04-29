package com.lab.repository;

import com.lab.entity.AuditLog;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AuditLogRepository implements InMemoryRepository<AuditLog, String> {
    private final Map<String, AuditLog> auditLogs = new ConcurrentHashMap<>();

    @Override
    public AuditLog save(AuditLog entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        auditLogs.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<AuditLog> findById(String id) {
        return Optional.ofNullable(auditLogs.get(id));
    }

    @Override
    public List<AuditLog> findAll() {
        List<AuditLog> logs = new ArrayList<>(auditLogs.values());
        logs.sort(Comparator.comparing(AuditLog::getOperationTime).reversed());
        return logs;
    }

    @Override
    public void deleteById(String id) {
        auditLogs.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return auditLogs.containsKey(id);
    }
}
