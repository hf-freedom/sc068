package com.lab.controller;

import com.lab.entity.PurchaseRequest;
import com.lab.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public List<PurchaseRequest> getAllPurchaseRequests() {
        return purchaseService.getAllPurchaseRequests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseRequest> getPurchaseRequestById(@PathVariable String id) {
        return purchaseService.getPurchaseRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PurchaseRequest createPurchaseRequest(@RequestBody PurchaseRequest request) {
        return purchaseService.createPurchaseRequest(request);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approvePurchaseRequest(
            @PathVariable String id,
            @RequestParam String approverId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remark) {
        logger.info("Approving purchase request: id={}, approverId={}, approved={}, remark={}",
                id, approverId, approved, remark);
        try {
            PurchaseRequest result = purchaseService.approvePurchaseRequest(id, approverId, approved, remark);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            logger.error("Error approving purchase request: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
