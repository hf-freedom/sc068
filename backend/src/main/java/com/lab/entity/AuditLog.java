package com.lab.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLog {
    private String id;
    private String operationType;
    private String relatedId;
    private String userId;
    private LocalDateTime operationTime;
    private String detail;
    private String ip;
}
