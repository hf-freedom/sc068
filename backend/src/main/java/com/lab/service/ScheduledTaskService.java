package com.lab.service;

import com.lab.entity.*;
import com.lab.entity.enums.ApprovalStatus;
import com.lab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduledTaskService {

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private ScrapTaskRepository scrapTaskRepository;

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void checkExpiredChemicals() {
        LocalDate today = LocalDate.now();
        List<Chemical> chemicals = chemicalRepository.findAll();

        for (Chemical chemical : chemicals) {
            if (chemical.getExpiryDate() != null &&
                    chemical.getExpiryDate().isBefore(today) &&
                    chemical.getCurrentStock() > 0) {

                boolean hasPendingScrapTask = scrapTaskRepository.findAll().stream()
                        .anyMatch(task -> task.getChemicalId().equals(chemical.getId()) &&
                                task.getApprovalStatus() == ApprovalStatus.PENDING);

                if (!hasPendingScrapTask) {
                    createScrapTask(chemical);
                }
            }
        }
    }

    private void createScrapTask(Chemical chemical) {
        ScrapTask task = new ScrapTask();
        task.setChemicalId(chemical.getId());
        task.setQuantity(chemical.getCurrentStock());
        task.setReason("危化品已过期");
        task.setGenerateTime(LocalDateTime.now());
        task.setApprovalStatus(ApprovalStatus.PENDING);

        ScrapTask saved = scrapTaskRepository.save(task);

        auditLogService.log("SCRAP_TASK_CREATE", saved.getId(), "system",
                "自动生成报废任务: " + chemical.getName() + ", 数量: " + chemical.getCurrentStock() +
                        ", 过期日期: " + chemical.getExpiryDate());
    }

    public List<ScrapTask> getAllScrapTasks() {
        return scrapTaskRepository.findAll();
    }

    public ScrapTask approveScrapTask(String taskId, String approverId, boolean approved, String remark) {
        Optional<ScrapTask> taskOpt = scrapTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("报废任务不存在");
        }
        ScrapTask task = taskOpt.get();

        if (task.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("报废任务状态不是待审批");
        }

        task.setApproverId(approverId);
        task.setApprovalStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        task.setProcessTime(LocalDateTime.now());
        task.setRemark(remark);

        if (approved) {
            Optional<Chemical> chemicalOpt = chemicalRepository.findById(task.getChemicalId());
            if (chemicalOpt.isPresent()) {
                Chemical chemical = chemicalOpt.get();

                List<Lab> labs = labRepository.findAll();
                for (Lab lab : labs) {
                    if (lab.getUsedCapacity() >= task.getQuantity()) {
                        lab.setUsedCapacity(lab.getUsedCapacity() - task.getQuantity());
                        labRepository.save(lab);
                        break;
                    }
                }

                chemical.setCurrentStock(chemical.getCurrentStock() - task.getQuantity());
                chemicalRepository.save(chemical);

                auditLogService.log("SCRAP_TASK_APPROVED", task.getId(), approverId,
                        "报废任务审批通过: " + chemical.getName() + ", 数量: " + task.getQuantity());
            }
        }

        ScrapTask saved = scrapTaskRepository.save(task);

        String action = approved ? "审批通过" : "审批拒绝";
        auditLogService.log("SCRAP_TASK_APPROVE", saved.getId(), approverId,
                action + "报废任务: " + saved.getId());

        return saved;
    }
}
