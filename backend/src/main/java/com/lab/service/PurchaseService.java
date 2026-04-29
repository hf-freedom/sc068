package com.lab.service;

import com.lab.entity.*;
import com.lab.entity.enums.ApprovalStatus;
import com.lab.entity.enums.DangerLevel;
import com.lab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRequestRepository purchaseRequestRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public PurchaseRequest createPurchaseRequest(PurchaseRequest request) {
        Optional<Lab> labOpt = labRepository.findById(request.getLabId());
        if (!labOpt.isPresent()) {
            throw new RuntimeException("实验室不存在");
        }
        Lab lab = labOpt.get();

        Optional<Chemical> chemicalOpt = chemicalRepository.findById(request.getChemicalId());
        if (!chemicalOpt.isPresent()) {
            throw new RuntimeException("危化品不存在");
        }
        Chemical chemical = chemicalOpt.get();

        double availableCapacity = lab.getStorageCapacity() - lab.getUsedCapacity();
        if (request.getQuantity() > availableCapacity) {
            throw new RuntimeException("实验室存储容量不足，可用容量: " + availableCapacity);
        }

        request.setRequestTime(LocalDateTime.now());
        request.setApprovalStatus(ApprovalStatus.PENDING);

        PurchaseRequest saved = purchaseRequestRepository.save(request);

        auditLogService.log("PURCHASE_REQUEST_CREATE", saved.getId(), request.getRequesterId(),
                "创建采购申请: " + chemical.getName() + ", 数量: " + request.getQuantity());

        return saved;
    }

    public List<PurchaseRequest> getAllPurchaseRequests() {
        return purchaseRequestRepository.findAll();
    }

    public Optional<PurchaseRequest> getPurchaseRequestById(String id) {
        return purchaseRequestRepository.findById(id);
    }

    @Transactional
    public PurchaseRequest approvePurchaseRequest(String requestId, String approverId, boolean approved, String remark) {
        Optional<PurchaseRequest> requestOpt = purchaseRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new RuntimeException("采购申请不存在");
        }
        PurchaseRequest request = requestOpt.get();

        if (request.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("采购申请状态不是待审批");
        }

        Optional<Chemical> chemicalOpt = chemicalRepository.findById(request.getChemicalId());
        boolean needDualApproval = chemicalOpt.isPresent() &&
                (chemicalOpt.get().getDangerLevel() == DangerLevel.HIGH ||
                 chemicalOpt.get().getDangerLevel() == DangerLevel.EXTREME);

        if (needDualApproval) {
            if (request.getFirstApproverId() == null) {
                request.setFirstApproverId(approverId);
                if (approved) {
                    request.setRemark("第一轮审批通过，等待第二轮审批");
                } else {
                    request.setApprovalStatus(ApprovalStatus.REJECTED);
                    request.setRemark(remark);
                }
            } else if (request.getSecondApproverId() == null) {
                if (request.getFirstApproverId().equals(approverId)) {
                    throw new RuntimeException("同一审批人不能审批两次");
                }
                request.setSecondApproverId(approverId);
                if (approved) {
                    request.setApprovalStatus(ApprovalStatus.APPROVED);
                    request.setRemark("双人审批通过");
                } else {
                    request.setApprovalStatus(ApprovalStatus.REJECTED);
                    request.setRemark(remark);
                }
            }
        } else {
            request.setFirstApproverId(approverId);
            request.setApprovalStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
            request.setRemark(remark);
        }

        request.setApprovalTime(LocalDateTime.now());
        PurchaseRequest saved = purchaseRequestRepository.save(request);

        String action = approved ? "审批通过" : "审批拒绝";
        auditLogService.log("PURCHASE_REQUEST_APPROVE", saved.getId(), approverId,
                action + "采购申请: " + saved.getId());

        return saved;
    }
}
