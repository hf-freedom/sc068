package com.lab.controller;

import com.lab.entity.Chemical;
import com.lab.service.ChemicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chemicals")
@CrossOrigin(origins = "*")
public class ChemicalController {

    @Autowired
    private ChemicalService chemicalService;

    @GetMapping
    public List<Chemical> getAllChemicals() {
        return chemicalService.getAllChemicals();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chemical> getChemicalById(@PathVariable String id) {
        return chemicalService.getChemicalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Chemical createChemical(@RequestBody Chemical chemical) {
        return chemicalService.createChemical(chemical);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chemical> updateChemical(@PathVariable String id, @RequestBody Chemical chemical) {
        return chemicalService.getChemicalById(id)
                .map(existing -> {
                    chemical.setId(id);
                    return ResponseEntity.ok(chemicalService.updateChemical(chemical));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChemical(@PathVariable String id) {
        if (chemicalService.getChemicalById(id).isPresent()) {
            chemicalService.deleteChemical(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
