package com.lab.entity;

import com.lab.entity.enums.DangerLevel;
import com.lab.entity.enums.StorageCondition;
import lombok.Data;

import java.util.List;

@Data
public class Lab {
    private String id;
    private String name;
    private String code;
    private DangerLevel safetyLevel;
    private String responsiblePersonId;
    private Double storageCapacity;
    private Double usedCapacity;
    private List<StorageCondition> availableConditions;
    private boolean canReceive;
}
