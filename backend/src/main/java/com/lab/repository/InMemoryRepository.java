package com.lab.repository;

import java.util.List;
import java.util.Optional;

public interface InMemoryRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}
