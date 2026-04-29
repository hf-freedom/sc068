package com.lab.entity;

import com.lab.entity.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsageRecord {
    private String id;
    private String chemicalId;
    private String userId;
    private String labId;
    private Double quantity;
    private String experimentProject;
    private ApprovalStatus approvalStatus;
    private String firstApproverId;
    private String secondApproverId;
    private LocalDateTime requestTime;
    private LocalDateTime useTime;
    private Double wasteQuantity;
    private String remark;
}
