package com.lab.service;

import com.lab.entity.*;
import com.lab.entity.enums.ApprovalStatus;
import com.lab.entity.enums.DangerLevel;
import com.lab.entity.enums.UserStatus;
import com.lab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsageService {

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public UsageRecord createUsageRequest(UsageRecord record) {
        Optional<User> userOpt = userRepository.findById(record.getUserId());
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        User user = userOpt.get();

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("用户状态异常，无法领用");
        }

        if (user.getQualifications() == null || user.getQualifications().isEmpty()) {
            throw new RuntimeException("用户无相关资质证书");
        }

        Optional<Chemical> chemicalOpt = chemicalRepository.findById(record.getChemicalId());
        if (!chemicalOpt.isPresent()) {
            throw new RuntimeException("危化品不存在");
        }
        Chemical chemical = chemicalOpt.get();

        if (user.getAllowedCategories() == null ||
                (!user.getAllowedCategories().contains("ALL") &&
                        user.getAllowedCategories().stream().noneMatch(cat ->
                                chemical.getName().contains(cat) || cat.contains(chemical.getName())))) {
            throw new RuntimeException("用户无领用该危化品的权限");
        }

        if (record.getQuantity() > user.getSingleLimit()) {
            throw new RuntimeException("超出单次领用限额，限额: " + user.getSingleLimit());
        }

        if (record.getQuantity() > chemical.getCurrentStock()) {
            throw new RuntimeException("库存不足，当前库存: " + chemical.getCurrentStock());
        }

        Optional<Lab> labOpt = labRepository.findById(record.getLabId());
        if (labOpt.isPresent() && !labOpt.get().isCanReceive()) {
            throw new RuntimeException("实验室已暂停领用权限");
        }

        record.setRequestTime(LocalDateTime.now());
        record.setApprovalStatus(ApprovalStatus.PENDING);

        UsageRecord saved = usageRecordRepository.save(record);

        auditLogService.log("USAGE_REQUEST_CREATE", saved.getId(), record.getUserId(),
                "创建领用申请: " + chemical.getName() + ", 数量: " + record.getQuantity());

        return saved;
    }

    public List<UsageRecord> getAllUsageRecords() {
        return usageRecordRepository.findAll();
    }

    public Optional<UsageRecord> getUsageRecordById(String id) {
        return usageRecordRepository.findById(id);
    }

    @Transactional
    public UsageRecord approveUsageRequest(String requestId, String approverId, boolean approved, String remark) {
        Optional<UsageRecord> recordOpt = usageRecordRepository.findById(requestId);
        if (!recordOpt.isPresent()) {
            throw new RuntimeException("领用申请不存在");
        }
        UsageRecord record = recordOpt.get();

        if (record.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("领用申请状态不是待审批");
        }

        Optional<Chemical> chemicalOpt = chemicalRepository.findById(record.getChemicalId());
        boolean needDualApproval = chemicalOpt.isPresent() &&
                (chemicalOpt.get().getDangerLevel() == DangerLevel.HIGH ||
                 chemicalOpt.get().getDangerLevel() == DangerLevel.EXTREME);

        if (needDualApproval) {
            if (record.getFirstApproverId() == null) {
                record.setFirstApproverId(approverId);
                if (approved) {
                    record.setRemark("第一轮审批通过，等待第二轮审批");
                } else {
                    record.setApprovalStatus(ApprovalStatus.REJECTED);
                    record.setRemark(remark);
                }
            } else if (record.getSecondApproverId() == null) {
                if (record.getFirstApproverId().equals(approverId)) {
                    throw new RuntimeException("同一审批人不能审批两次");
                }
                record.setSecondApproverId(approverId);
                if (approved) {
                    completeUsage(record);
                    record.setApprovalStatus(ApprovalStatus.APPROVED);
                    record.setRemark("双人审批通过");
                } else {
                    record.setApprovalStatus(ApprovalStatus.REJECTED);
                    record.setRemark(remark);
                }
            }
        } else {
            record.setFirstApproverId(approverId);
            if (approved) {
                completeUsage(record);
                record.setApprovalStatus(ApprovalStatus.APPROVED);
            } else {
                record.setApprovalStatus(ApprovalStatus.REJECTED);
            }
            record.setRemark(remark);
        }

        UsageRecord saved = usageRecordRepository.save(record);

        String action = approved ? "审批通过" : "审批拒绝";
        auditLogService.log("USAGE_REQUEST_APPROVE", saved.getId(), approverId,
                action + "领用申请: " + saved.getId());

        return saved;
    }

    private void completeUsage(UsageRecord record) {
        Optional<Chemical> chemicalOpt = chemicalRepository.findById(record.getChemicalId());
        if (chemicalOpt.isPresent()) {
            Chemical chemical = chemicalOpt.get();
            chemical.setCurrentStock(chemical.getCurrentStock() - record.getQuantity());
            chemicalRepository.save(chemical);

            Optional<Lab> labOpt = labRepository.findById(record.getLabId());
            if (labOpt.isPresent()) {
                Lab lab = labOpt.get();
                lab.setUsedCapacity(lab.getUsedCapacity() - record.getQuantity());
                labRepository.save(lab);
            }
        }
        record.setUseTime(LocalDateTime.now());
    }
}
