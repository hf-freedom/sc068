package com.lab.entity;

import com.lab.entity.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PurchaseRequest {
    private String id;
    private String chemicalId;
    private Double quantity;
    private String labId;
    private String requesterId;
    private ApprovalStatus approvalStatus;
    private String firstApproverId;
    private String secondApproverId;
    private LocalDateTime requestTime;
    private LocalDateTime approvalTime;
    private String remark;
}
