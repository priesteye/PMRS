// src/main/java/com/pmrs/repository/Repository.java
package com.pmrs.repository;

import com.pmrs.exception.PMRSException;
import com.pmrs.exception.RecordNotFoundException;
import java.util.List;

/**
 * Generic data access seam.
 * Allows swapping in-memory stores for file/DB stores in future iterations.
 */
public interface Repository<T> {

    void add(T entity) throws PMRSException;

    T findById(String id) throws RecordNotFoundException;

    List<T> findAll();

    void update(T entity) throws RecordNotFoundException;

    void delete(String id) throws RecordNotFoundException;
}