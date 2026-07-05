// src/main/java/com/pmrs/repository/InMemoryMedicalRecordRepository.java
package com.pmrs.repository;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.RecordNotFoundException;
import com.pmrs.model.MedicalRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryMedicalRecordRepository implements Repository<MedicalRecord> {

    private final Map<String, MedicalRecord> store = new HashMap<>();

    @Override
    public void add(MedicalRecord entity) throws PMRSException {
        if (store.containsKey(entity.getId())) {
            throw new PMRSException("Medical Record with ID " + entity.getId() + " already exists.");
        }
        store.put(entity.getId(), entity);
    }

    @Override
    public MedicalRecord findById(String id) throws RecordNotFoundException {
        MedicalRecord record = store.get(id);
        if (record == null) {
            throw new RecordNotFoundException("No medical record found with ID: " + id);
        }
        return record;
    }

    @Override
    public List<MedicalRecord> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(MedicalRecord entity) throws RecordNotFoundException {
        if (!store.containsKey(entity.getId())) {
            throw new RecordNotFoundException("Cannot update. Medical Record not found with ID: " + entity.getId());
        }
        store.put(entity.getId(), entity);
    }

    @Override
    public void delete(String id) throws RecordNotFoundException {
        if (store.remove(id) == null) {
            throw new RecordNotFoundException("Cannot delete. Medical Record not found with ID: " + id);
        }
    }
}