package com.lab.repository;

import com.lab.entity.RecoveryTask;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RecoveryTaskRepository implements InMemoryRepository<RecoveryTask, String> {
    private final Map<String, RecoveryTask> recoveryTasks = new ConcurrentHashMap<>();

    @Override
    public RecoveryTask save(RecoveryTask entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        recoveryTasks.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<RecoveryTask> findById(String id) {
        return Optional.ofNullable(recoveryTasks.get(id));
    }

    @Override
    public List<RecoveryTask> findAll() {
        return new ArrayList<>(recoveryTasks.values());
    }

    @Override
    public void deleteById(String id) {
        recoveryTasks.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return recoveryTasks.containsKey(id);
    }
}
