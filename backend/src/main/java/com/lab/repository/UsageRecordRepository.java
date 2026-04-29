package com.lab.repository;

import com.lab.entity.UsageRecord;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UsageRecordRepository implements InMemoryRepository<UsageRecord, String> {
    private final Map<String, UsageRecord> usageRecords = new ConcurrentHashMap<>();

    @Override
    public UsageRecord save(UsageRecord entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        usageRecords.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<UsageRecord> findById(String id) {
        return Optional.ofNullable(usageRecords.get(id));
    }

    @Override
    public List<UsageRecord> findAll() {
        return new ArrayList<>(usageRecords.values());
    }

    @Override
    public void deleteById(String id) {
        usageRecords.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return usageRecords.containsKey(id);
    }
}
