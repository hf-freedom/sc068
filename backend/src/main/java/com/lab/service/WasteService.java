package com.lab.service;

import com.lab.entity.*;
import com.lab.entity.enums.ApprovalStatus;
import com.lab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WasteService {

    private static final double WASTE_THRESHOLD = 100.0;

    @Autowired
    private WasteRecordRepository wasteRecordRepository;

    @Autowired
    private RecoveryTaskRepository recoveryTaskRepository;

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public WasteRecord recordWaste(WasteRecord record) {
        Optional<UsageRecord> usageOpt = usageRecordRepository.findById(record.getId());
        if (usageOpt.isPresent() && usageOpt.get().getWasteQuantity() == null) {
            UsageRecord usage = usageOpt.get();
            if (record.getQuantity() > usage.getQuantity()) {
                throw new RuntimeException("废弃物重量不能超过领用数量");
            }
            usage.setWasteQuantity(record.getQuantity());
            usageRecordRepository.save(usage);
        }

        record.setGenerateTime(LocalDateTime.now());
        WasteRecord saved = wasteRecordRepository.save(record);

        Optional<Chemical> chemicalOpt = chemicalRepository.findById(record.getChemicalId());
        String chemicalName = chemicalOpt.map(Chemical::getName).orElse("未知");

        auditLogService.log("WASTE_RECORD", saved.getId(), record.getUserId(),
                "登记废弃物: " + chemicalName + ", 重量: " + record.getQuantity() + " kg");

        checkAndCreateRecoveryTask(record.getLabId());

        return saved;
    }

    private void checkAndCreateRecoveryTask(String labId) {
        List<WasteRecord> unprocessedWastes = wasteRecordRepository.findAll().stream()
                .filter(w -> w.getLabId().equals(labId))
                .filter(w -> !isWasteInRecoveryTask(w.getId()))
                .collect(Collectors.toList());

        double totalWeight = unprocessedWastes.stream()
                .mapToDouble(WasteRecord::getQuantity)
                .sum();

        if (totalWeight >= WASTE_THRESHOLD) {
            RecoveryTask task = new RecoveryTask();
            task.setLabId(labId);
            task.setWasteRecordIds(unprocessedWastes.stream()
                    .map(WasteRecord::getId)
                    .collect(Collectors.toList()));
            task.setTotalQuantity(totalWeight);
            task.setGenerateTime(LocalDateTime.now());
            task.setApprovalStatus(ApprovalStatus.PENDING);

            RecoveryTask savedTask = recoveryTaskRepository.save(task);

            Optional<Lab> labOpt = labRepository.findById(labId);
            String labName = labOpt.map(Lab::getName).orElse("未知");

            auditLogService.log("RECOVERY_TASK_CREATE", savedTask.getId(), "system",
                    "自动生成回收任务: 实验室 " + labName + ", 总重量: " + totalWeight + " kg");
        }
    }

    private boolean isWasteInRecoveryTask(String wasteId) {
        return recoveryTaskRepository.findAll().stream()
                .anyMatch(task -> task.getWasteRecordIds() != null &&
                        task.getWasteRecordIds().contains(wasteId) &&
                        task.getApprovalStatus() != ApprovalStatus.CANCELLED);
    }

    public List<WasteRecord> getAllWasteRecords() {
        return wasteRecordRepository.findAll();
    }

    public List<RecoveryTask> getAllRecoveryTasks() {
        return recoveryTaskRepository.findAll();
    }

    @Transactional
    public RecoveryTask approveRecoveryTask(String taskId, String approverId, boolean approved, String remark) {
        Optional<RecoveryTask> taskOpt = recoveryTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("回收任务不存在");
        }
        RecoveryTask task = taskOpt.get();

        if (task.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("回收任务状态不是待审批");
        }

        task.setApproverId(approverId);
        task.setApprovalStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        task.setProcessTime(LocalDateTime.now());
        task.setRemark(remark);

        RecoveryTask saved = recoveryTaskRepository.save(task);

        String action = approved ? "审批通过" : "审批拒绝";
        auditLogService.log("RECOVERY_TASK_APPROVE", saved.getId(), approverId,
                action + "回收任务: " + saved.getId() + ", 总重量: " + saved.getTotalQuantity() + " kg");

        return saved;
    }
}
