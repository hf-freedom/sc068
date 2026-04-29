package com.lab.entity;

import com.lab.entity.enums.DangerLevel;
import com.lab.entity.enums.StorageCondition;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Chemical {
    private String id;
    private String name;
    private String casNumber;
    private DangerLevel dangerLevel;
    private StorageCondition storageCondition;
    private Double maxStock;
    private Double currentStock;
    private LocalDate expiryDate;
    private String unit;
    private String description;
}
