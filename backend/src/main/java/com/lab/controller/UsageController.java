package com.lab.controller;

import com.lab.entity.UsageRecord;
import com.lab.service.UsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usages")
@CrossOrigin(origins = "*")
public class UsageController {

    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);

    @Autowired
    private UsageService usageService;

    @GetMapping
    public List<UsageRecord> getAllUsageRecords() {
        return usageService.getAllUsageRecords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageRecord> getUsageRecordById(@PathVariable String id) {
        return usageService.getUsageRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUsageRequest(@RequestBody UsageRecord record) {
        try {
            UsageRecord result = usageService.createUsageRequest(record);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("Error creating usage request: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveUsageRequest(
            @PathVariable String id,
            @RequestParam String approverId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remark) {
        logger.info("Approving usage request: id={}, approverId={}, approved={}, remark={}",
                id, approverId, approved, remark);
        try {
            UsageRecord result = usageService.approveUsageRequest(id, approverId, approved, remark);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("Error approving usage request: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
