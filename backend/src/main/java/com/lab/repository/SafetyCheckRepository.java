package com.lab.repository;

import com.lab.entity.SafetyCheck;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SafetyCheckRepository implements InMemoryRepository<SafetyCheck, String> {
    private final Map<String, SafetyCheck> safetyChecks = new ConcurrentHashMap<>();

    @Override
    public SafetyCheck save(SafetyCheck entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        safetyChecks.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<SafetyCheck> findById(String id) {
        return Optional.ofNullable(safetyChecks.get(id));
    }

    @Override
    public List<SafetyCheck> findAll() {
        return new ArrayList<>(safetyChecks.values());
    }

    @Override
    public void deleteById(String id) {
        safetyChecks.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return safetyChecks.containsKey(id);
    }
}
