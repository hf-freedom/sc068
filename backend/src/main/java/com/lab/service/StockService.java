package com.lab.service;

import com.lab.entity.*;
import com.lab.entity.enums.ApprovalStatus;
import com.lab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockInRecordRepository stockInRecordRepository;

    @Autowired
    private PurchaseRequestRepository purchaseRequestRepository;

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public StockInRecord stockIn(StockInRecord record) {
        Optional<PurchaseRequest> requestOpt = purchaseRequestRepository.findById(record.getPurchaseRequestId());
        if (!requestOpt.isPresent()) {
            throw new RuntimeException("采购申请不存在");
        }
        PurchaseRequest purchaseRequest = requestOpt.get();

        if (purchaseRequest.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("采购申请未审批通过");
        }

        Optional<Lab> labOpt = labRepository.findById(record.getLabId());
        if (!labOpt.isPresent()) {
            throw new RuntimeException("实验室不存在");
        }
        Lab lab = labOpt.get();

        if (!lab.isCanReceive()) {
            throw new RuntimeException("实验室已暂停领用权限，无法入库");
        }

        Optional<Chemical> chemicalOpt = chemicalRepository.findById(record.getChemicalId());
        if (!chemicalOpt.isPresent()) {
            throw new RuntimeException("危化品不存在");
        }
        Chemical chemical = chemicalOpt.get();

        if (!lab.getAvailableConditions().contains(chemical.getStorageCondition())) {
            throw new RuntimeException("实验室不具备该危化品的存储条件: " + chemical.getStorageCondition().getDescription());
        }

        double newStock = chemical.getCurrentStock() + record.getQuantity();
        if (newStock > chemical.getMaxStock()) {
            throw new RuntimeException("超出最大库存限制，当前库存: " + chemical.getCurrentStock() +
                    ", 最大库存: " + chemical.getMaxStock());
        }

        double availableCapacity = lab.getStorageCapacity() - lab.getUsedCapacity();
        if (record.getQuantity() > availableCapacity) {
            throw new RuntimeException("实验室存储容量不足，可用容量: " + availableCapacity);
        }

        chemical.setCurrentStock(newStock);
        chemicalRepository.save(chemical);

        lab.setUsedCapacity(lab.getUsedCapacity() + record.getQuantity());
        labRepository.save(lab);

        record.setStockInTime(LocalDateTime.now());
        StockInRecord saved = stockInRecordRepository.save(record);

        auditLogService.log("STOCK_IN", saved.getId(), record.getOperatorId(),
                "入库: " + chemical.getName() + ", 数量: " + record.getQuantity() + ", 实验室: " + lab.getName());

        return saved;
    }

    public List<StockInRecord> getAllStockInRecords() {
        return stockInRecordRepository.findAll();
    }

    public Optional<StockInRecord> getStockInRecordById(String id) {
        return stockInRecordRepository.findById(id);
    }
}
