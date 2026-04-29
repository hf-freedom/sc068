package com.lab.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SafetyCheck {
    private String id;
    private String labId;
    private LocalDateTime checkTime;
    private String checkerId;
    private boolean passed;
    private List<String> issues;
    private String remark;
}
