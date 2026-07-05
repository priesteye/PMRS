// src/main/java/com/pmrs/repository/InMemoryPhysicianRepository.java
package com.pmrs.repository;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.RecordNotFoundException;
import com.pmrs.model.Physician;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPhysicianRepository implements Repository<Physician> {

    private final Map<String, Physician> store = new HashMap<>();

    @Override
    public void add(Physician entity) throws PMRSException {
        if (store.containsKey(entity.getId())) {
            throw new PMRSException("Physician with ID " + entity.getId() + " already exists.");
        }
        store.put(entity.getId(), entity);
    }

    @Override
    public Physician findById(String id) throws RecordNotFoundException {
        Physician physician = store.get(id);
        if (physician == null) {
            throw new RecordNotFoundException("No physician found with ID: " + id);
        }
        return physician;
    }

    @Override
    public List<Physician> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(Physician entity) throws RecordNotFoundException {
        if (!store.containsKey(entity.getId())) {
            throw new RecordNotFoundException("Cannot update. Physician not found with ID: " + entity.getId());
        }
        store.put(entity.getId(), entity);
    }

    @Override
    public void delete(String id) throws RecordNotFoundException {
        if (store.remove(id) == null) {
            throw new RecordNotFoundException("Cannot delete. Physician not found with ID: " + id);
        }
    }
}