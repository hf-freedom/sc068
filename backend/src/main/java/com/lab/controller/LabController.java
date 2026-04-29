package com.lab.controller;

import com.lab.entity.Lab;
import com.lab.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labs")
@CrossOrigin(origins = "*")
public class LabController {

    @Autowired
    private LabService labService;

    @GetMapping
    public List<Lab> getAllLabs() {
        return labService.getAllLabs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lab> getLabById(@PathVariable String id) {
        return labService.getLabById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Lab createLab(@RequestBody Lab lab) {
        return labService.createLab(lab);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lab> updateLab(@PathVariable String id, @RequestBody Lab lab) {
        return labService.getLabById(id)
                .map(existing -> {
                    lab.setId(id);
                    return ResponseEntity.ok(labService.updateLab(lab));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLab(@PathVariable String id) {
        if (labService.getLabById(id).isPresent()) {
            labService.deleteLab(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
