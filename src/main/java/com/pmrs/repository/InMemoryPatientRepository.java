// src/main/java/com/pmrs/repository/InMemoryPatientRepository.java
package com.pmrs.repository;

import com.pmrs.exception.DuplicatePatientException;
import com.pmrs.exception.RecordNotFoundException;
import com.pmrs.model.Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPatientRepository implements Repository<Patient> {

    // Backing store per Section 7 constraints
    private final Map<String, Patient> store = new HashMap<>();

    @Override
    public void add(Patient entity) throws DuplicatePatientException {
        if (store.containsKey(entity.getId())) {
            throw new DuplicatePatientException("Patient with ID " + entity.getId() + " already exists.");
        }
        store.put(entity.getId(), entity);
    }

    @Override
    public Patient findById(String id) throws RecordNotFoundException {
        Patient patient = store.get(id);
        if (patient == null) {
            throw new RecordNotFoundException("No patient found with ID: " + id);
        }
        return patient;
    }

    @Override
    public List<Patient> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(Patient entity) throws RecordNotFoundException {
        if (!store.containsKey(entity.getId())) {
            throw new RecordNotFoundException("Cannot update. Patient not found with ID: " + entity.getId());
        }
        store.put(entity.getId(), entity);
    }

    @Override
    public void delete(String id) throws RecordNotFoundException {
        if (store.remove(id) == null) {
            throw new RecordNotFoundException("Cannot delete. Patient not found with ID: " + id);
        }
    }
}