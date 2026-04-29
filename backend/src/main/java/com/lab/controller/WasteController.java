package com.lab.controller;

import com.lab.entity.RecoveryTask;
import com.lab.entity.WasteRecord;
import com.lab.service.WasteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/waste")
@CrossOrigin(origins = "*")
public class WasteController {

    private static final Logger logger = LoggerFactory.getLogger(WasteController.class);

    @Autowired
    private WasteService wasteService;

    @GetMapping("/records")
    public List<WasteRecord> getAllWasteRecords() {
        return wasteService.getAllWasteRecords();
    }

    @PostMapping("/record")
    public ResponseEntity<?> recordWaste(@RequestBody WasteRecord record) {
        try {
            WasteRecord result = wasteService.recordWaste(record);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("Error recording waste: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/recovery-tasks")
    public List<RecoveryTask> getAllRecoveryTasks() {
        return wasteService.getAllRecoveryTasks();
    }

    @PostMapping("/recovery-tasks/{id}/approve")
    public ResponseEntity<?> approveRecoveryTask(
            @PathVariable String id,
            @RequestParam String approverId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remark) {
        logger.info("Approving recovery task: id={}, approverId={}, approved={}, remark={}",
                id, approverId, approved, remark);
        try {
            RecoveryTask result = wasteService.approveRecoveryTask(id, approverId, approved, remark);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("Error approving recovery task: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
