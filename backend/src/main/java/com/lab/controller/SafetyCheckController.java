package com.lab.controller;

import com.lab.entity.Lab;
import com.lab.entity.SafetyCheck;
import com.lab.service.SafetyCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/safety-checks")
@CrossOrigin(origins = "*")
public class SafetyCheckController {

    @Autowired
    private SafetyCheckService safetyCheckService;

    @GetMapping
    public List<SafetyCheck> getAllSafetyChecks() {
        return safetyCheckService.getAllSafetyChecks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SafetyCheck> getSafetyCheckById(@PathVariable String id) {
        return safetyCheckService.getSafetyCheckById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> performSafetyCheck(@RequestBody SafetyCheck check) {
        try {
            SafetyCheck result = safetyCheckService.performSafetyCheck(check);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/labs/{labId}/resume")
    public ResponseEntity<?> resumeLabAccess(
            @PathVariable String labId,
            @RequestParam String operatorId,
            @RequestParam String reason) {
        try {
            Lab result = safetyCheckService.resumeLabAccess(labId, operatorId, reason);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
