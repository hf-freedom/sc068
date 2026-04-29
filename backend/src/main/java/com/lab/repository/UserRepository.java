package com.lab.repository;

import com.lab.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository implements InMemoryRepository<User, String> {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        users.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(String id) {
        users.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return users.containsKey(id);
    }
}
