package com.lab.service;

import com.lab.entity.*;
import com.lab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SafetyCheckService {

    @Autowired
    private SafetyCheckRepository safetyCheckRepository;

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public SafetyCheck performSafetyCheck(SafetyCheck check) {
        Optional<Lab> labOpt = labRepository.findById(check.getLabId());
        if (!labOpt.isPresent()) {
            throw new RuntimeException("实验室不存在");
        }
        Lab lab = labOpt.get();

        check.setCheckTime(LocalDateTime.now());
        SafetyCheck saved = safetyCheckRepository.save(check);

        if (!check.isPassed()) {
            lab.setCanReceive(false);
            labRepository.save(lab);
            auditLogService.log("SAFETY_CHECK_FAILED", saved.getId(), check.getCheckerId(),
                    "安全检查不通过，实验室 " + lab.getName() + " 暂停领用权限。问题: " + check.getIssues());
        } else {
            lab.setCanReceive(true);
            labRepository.save(lab);
            auditLogService.log("SAFETY_CHECK_PASSED", saved.getId(), check.getCheckerId(),
                    "安全检查通过，实验室 " + lab.getName() + " 恢复领用权限");
        }

        return saved;
    }

    @Transactional
    public Lab resumeLabAccess(String labId, String operatorId, String reason) {
        Optional<Lab> labOpt = labRepository.findById(labId);
        if (!labOpt.isPresent()) {
            throw new RuntimeException("实验室不存在");
        }
        Lab lab = labOpt.get();

        lab.setCanReceive(true);
        Lab saved = labRepository.save(lab);

        auditLogService.log("LAB_ACCESS_RESUMED", labId, operatorId,
                "实验室 " + lab.getName() + " 恢复领用权限。原因: " + reason);

        return saved;
    }

    public List<SafetyCheck> getAllSafetyChecks() {
        return safetyCheckRepository.findAll();
    }

    public Optional<SafetyCheck> getSafetyCheckById(String id) {
        return safetyCheckRepository.findById(id);
    }
}
