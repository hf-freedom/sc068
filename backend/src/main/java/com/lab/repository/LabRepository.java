package com.lab.repository;

import com.lab.entity.Lab;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LabRepository implements InMemoryRepository<Lab, String> {
    private final Map<String, Lab> labs = new ConcurrentHashMap<>();

    @Override
    public Lab save(Lab entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        labs.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Lab> findById(String id) {
        return Optional.ofNullable(labs.get(id));
    }

    @Override
    public List<Lab> findAll() {
        return new ArrayList<>(labs.values());
    }

    @Override
    public void deleteById(String id) {
        labs.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return labs.containsKey(id);
    }
}
