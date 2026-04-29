package com.lab.repository;

import com.lab.entity.StockInRecord;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StockInRecordRepository implements InMemoryRepository<StockInRecord, String> {
    private final Map<String, StockInRecord> stockInRecords = new ConcurrentHashMap<>();

    @Override
    public StockInRecord save(StockInRecord entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        stockInRecords.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<StockInRecord> findById(String id) {
        return Optional.ofNullable(stockInRecords.get(id));
    }

    @Override
    public List<StockInRecord> findAll() {
        return new ArrayList<>(stockInRecords.values());
    }

    @Override
    public void deleteById(String id) {
        stockInRecords.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return stockInRecords.containsKey(id);
    }
}
