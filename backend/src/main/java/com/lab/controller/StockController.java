package com.lab.controller;

import com.lab.entity.StockInRecord;
import com.lab.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "*")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public List<StockInRecord> getAllStockInRecords() {
        return stockService.getAllStockInRecords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockInRecord> getStockInRecordById(@PathVariable String id) {
        return stockService.getStockInRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/in")
    public ResponseEntity<?> stockIn(@RequestBody StockInRecord record) {
        try {
            StockInRecord result = stockService.stockIn(record);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
