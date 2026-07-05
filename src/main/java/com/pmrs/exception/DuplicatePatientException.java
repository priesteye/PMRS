// src/main/java/com/pmrs/exception/DuplicatePatientException.java
package com.pmrs.exception;

/**
 * Thrown when attempting to register a patient that already exists in the system.
 */
public class DuplicatePatientException extends PMRSException {

    public DuplicatePatientException(String message) {
        super(message);
    }

    public DuplicatePatientException(String message, Throwable cause) {
        super(message, cause);
    }
}