package com.lab.entity;

import com.lab.entity.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScrapTask {
    private String id;
    private String chemicalId;
    private String labId;
    private Double quantity;
    private String reason;
    private ApprovalStatus approvalStatus;
    private String approverId;
    private LocalDateTime generateTime;
    private LocalDateTime processTime;
    private String remark;
}
