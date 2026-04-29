package com.lab.entity;

import com.lab.entity.enums.UserStatus;
import lombok.Data;

import java.util.List;

@Data
public class User {
    private String id;
    private String name;
    private String employeeId;
    private String labId;
    private UserStatus status;
    private List<String> qualifications;
    private List<String> allowedCategories;
    private Double singleLimit;
}
