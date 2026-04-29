package com.lab.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockInRecord {
    private String id;
    private String purchaseRequestId;
    private String chemicalId;
    private String labId;
    private Double quantity;
    private String operatorId;
    private LocalDateTime stockInTime;
    private String batchNumber;
    private String remark;
}
