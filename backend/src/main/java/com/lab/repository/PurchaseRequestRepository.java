package com.lab.repository;

import com.lab.entity.PurchaseRequest;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PurchaseRequestRepository implements InMemoryRepository<PurchaseRequest, String> {
    private final Map<String, PurchaseRequest> purchaseRequests = new ConcurrentHashMap<>();

    @Override
    public PurchaseRequest save(PurchaseRequest entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        purchaseRequests.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<PurchaseRequest> findById(String id) {
        return Optional.ofNullable(purchaseRequests.get(id));
    }

    @Override
    public List<PurchaseRequest> findAll() {
        return new ArrayList<>(purchaseRequests.values());
    }

    @Override
    public void deleteById(String id) {
        purchaseRequests.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return purchaseRequests.containsKey(id);
    }
}
