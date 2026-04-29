package com.lab.service;

import com.lab.entity.Lab;
import com.lab.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LabService {

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private AuditLogService auditLogService;

    public Lab createLab(Lab lab) {
        if (lab.getUsedCapacity() == null) {
            lab.setUsedCapacity(0.0);
        }
        if (lab.isCanReceive() == false) {
            lab.setCanReceive(true);
        }
        Lab saved = labRepository.save(lab);
        auditLogService.log("LAB_CREATE", saved.getId(), "system",
                "创建实验室: " + saved.getName());
        return saved;
    }

    public Lab updateLab(Lab lab) {
        Lab saved = labRepository.save(lab);
        auditLogService.log("LAB_UPDATE", saved.getId(), "system",
                "更新实验室: " + saved.getName());
        return saved;
    }

    public void deleteLab(String id) {
        Optional<Lab> labOpt = labRepository.findById(id);
        if (labOpt.isPresent()) {
            Lab lab = labOpt.get();
            labRepository.deleteById(id);
            auditLogService.log("LAB_DELETE", id, "system",
                    "删除实验室: " + lab.getName());
        }
    }

    public List<Lab> getAllLabs() {
        return labRepository.findAll();
    }

    public Optional<Lab> getLabById(String id) {
        return labRepository.findById(id);
    }
}
