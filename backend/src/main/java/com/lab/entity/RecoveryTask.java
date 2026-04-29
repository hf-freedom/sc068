package com.lab.entity;

import com.lab.entity.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecoveryTask {
    private String id;
    private String labId;
    private List<String> wasteRecordIds;
    private Double totalQuantity;
    private ApprovalStatus approvalStatus;
    private String approverId;
    private LocalDateTime generateTime;
    private LocalDateTime processTime;
    private String remark;
}
