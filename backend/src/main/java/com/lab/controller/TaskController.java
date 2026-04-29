package com.lab.controller;

import com.lab.entity.ScrapTask;
import com.lab.service.ScheduledTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @GetMapping("/scrap")
    public List<ScrapTask> getAllScrapTasks() {
        return scheduledTaskService.getAllScrapTasks();
    }

    @PostMapping("/scrap/{id}/approve")
    public ResponseEntity<?> approveScrapTask(
            @PathVariable String id,
            @RequestParam String approverId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remark) {
        logger.info("Approving scrap task: id={}, approverId={}, approved={}, remark={}",
                id, approverId, approved, remark);
        try {
            ScrapTask result = scheduledTaskService.approveScrapTask(id, approverId, approved, remark);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("Error approving scrap task: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/scrap/check-expired")
    public ResponseEntity<String> checkExpiredChemicals() {
        scheduledTaskService.checkExpiredChemicals();
        return ResponseEntity.ok("已检查过期危化品并生成相应报废任务");
    }
}
