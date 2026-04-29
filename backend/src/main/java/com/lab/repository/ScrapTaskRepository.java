package com.lab.repository;

import com.lab.entity.ScrapTask;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ScrapTaskRepository implements InMemoryRepository<ScrapTask, String> {
    private final Map<String, ScrapTask> scrapTasks = new ConcurrentHashMap<>();

    @Override
    public ScrapTask save(ScrapTask entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        scrapTasks.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<ScrapTask> findById(String id) {
        return Optional.ofNullable(scrapTasks.get(id));
    }

    @Override
    public List<ScrapTask> findAll() {
        return new ArrayList<>(scrapTasks.values());
    }

    @Override
    public void deleteById(String id) {
        scrapTasks.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return scrapTasks.containsKey(id);
    }
}
