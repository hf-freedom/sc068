package com.lab.service;

import com.lab.entity.Chemical;
import com.lab.repository.ChemicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChemicalService {

    @Autowired
    private ChemicalRepository chemicalRepository;

    @Autowired
    private AuditLogService auditLogService;

    public Chemical createChemical(Chemical chemical) {
        if (chemical.getCurrentStock() == null) {
            chemical.setCurrentStock(0.0);
        }
        Chemical saved = chemicalRepository.save(chemical);
        auditLogService.log("CHEMICAL_CREATE", saved.getId(), "system",
                "创建危化品: " + saved.getName());
        return saved;
    }

    public Chemical updateChemical(Chemical chemical) {
        Chemical saved = chemicalRepository.save(chemical);
        auditLogService.log("CHEMICAL_UPDATE", saved.getId(), "system",
                "更新危化品: " + saved.getName());
        return saved;
    }

    public void deleteChemical(String id) {
        Optional<Chemical> chemicalOpt = chemicalRepository.findById(id);
        if (chemicalOpt.isPresent()) {
            Chemical chemical = chemicalOpt.get();
            chemicalRepository.deleteById(id);
            auditLogService.log("CHEMICAL_DELETE", id, "system",
                    "删除危化品: " + chemical.getName());
        }
    }

    public List<Chemical> getAllChemicals() {
        return chemicalRepository.findAll();
    }

    public Optional<Chemical> getChemicalById(String id) {
        return chemicalRepository.findById(id);
    }
}
