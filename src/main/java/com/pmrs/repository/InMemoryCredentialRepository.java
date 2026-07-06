// src/main/java/com/pmrs/repository/InMemoryCredentialRepository.java
package com.pmrs.repository;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.RecordNotFoundException;
import com.pmrs.model.Credentials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCredentialRepository implements Repository<Credentials> {
    private final Map<String, Credentials> store = new HashMap<>();

    @Override
    public void add(Credentials entity) throws PMRSException { store.put(entity.getId(), entity); }

    @Override
    public Credentials findById(String id) throws RecordNotFoundException {
        if (!store.containsKey(id)) throw new RecordNotFoundException("Credentials not found.");
        return store.get(id);
    }

    @Override public List<Credentials> findAll() { return new ArrayList<>(store.values()); }
    @Override public void update(Credentials entity) { store.put(entity.getId(), entity); }
    @Override public void delete(String id) { store.remove(id); }
}