// src/main/java/com/pmrs/repository/InMemoryAppointmentRepository.java
package com.pmrs.repository;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.RecordNotFoundException;
import com.pmrs.model.Appointment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryAppointmentRepository implements Repository<Appointment> {

    private final Map<String, Appointment> store = new HashMap<>();

    @Override
    public void add(Appointment entity) throws PMRSException {
        if (store.containsKey(entity.getId())) {
            throw new PMRSException("Appointment with ID " + entity.getId() + " already exists.");
        }
        store.put(entity.getId(), entity);
    }

    @Override
    public Appointment findById(String id) throws RecordNotFoundException {
        Appointment appointment = store.get(id);
        if (appointment == null) {
            throw new RecordNotFoundException("No appointment found with ID: " + id);
        }
        return appointment;
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(Appointment entity) throws RecordNotFoundException {
        if (!store.containsKey(entity.getId())) {
            throw new RecordNotFoundException("Cannot update. Appointment not found with ID: " + entity.getId());
        }
        store.put(entity.getId(), entity);
    }

    @Override
    public void delete(String id) throws RecordNotFoundException {
        if (store.remove(id) == null) {
            throw new RecordNotFoundException("Cannot delete. Appointment not found with ID: " + id);
        }
    }
}