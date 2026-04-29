package com.lab.repository;

import com.lab.entity.WasteRecord;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class WasteRecordRepository implements InMemoryRepository<WasteRecord, String> {
    private final Map<String, WasteRecord> wasteRecords = new ConcurrentHashMap<>();

    @Override
    public WasteRecord save(WasteRecord entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        wasteRecords.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<WasteRecord> findById(String id) {
        return Optional.ofNullable(wasteRecords.get(id));
    }

    @Override
    public List<WasteRecord> findAll() {
        return new ArrayList<>(wasteRecords.values());
    }

    @Override
    public void deleteById(String id) {
        wasteRecords.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return wasteRecords.containsKey(id);
    }
}
