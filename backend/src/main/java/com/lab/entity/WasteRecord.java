package com.lab.entity;

import com.lab.entity.enums.DangerLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WasteRecord {
    private String id;
    private String chemicalId;
    private String userId;
    private String labId;
    private Double quantity;
    private DangerLevel dangerLevel;
    private LocalDateTime generateTime;
    private String remark;
}
