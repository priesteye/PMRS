// src/main/java/com/pmrs/exception/RecordNotFoundException.java
package com.pmrs.exception;

/**
 * Thrown by repositories or services when a requested entity
 * (Patient, Physician, Appointment, etc.) cannot be found.
 */
public class RecordNotFoundException extends PMRSException {

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}