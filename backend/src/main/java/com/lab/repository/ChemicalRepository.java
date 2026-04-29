package com.lab.repository;

import com.lab.entity.Chemical;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChemicalRepository implements InMemoryRepository<Chemical, String> {
    private final Map<String, Chemical> chemicals = new ConcurrentHashMap<>();

    @Override
    public Chemical save(Chemical entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        chemicals.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Chemical> findById(String id) {
        return Optional.ofNullable(chemicals.get(id));
    }

    @Override
    public List<Chemical> findAll() {
        return new ArrayList<>(chemicals.values());
    }

    @Override
    public void deleteById(String id) {
        chemicals.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return chemicals.containsKey(id);
    }
}
