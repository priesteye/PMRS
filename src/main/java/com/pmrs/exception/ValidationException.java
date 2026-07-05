// src/main/java/com/pmrs/exception/ValidationException.java
package com.pmrs.exception;

/**
 * Thrown when an entity fails business validation rules
 * (e.g., invalid date of birth, empty required fields).
 */
public class ValidationException extends PMRSException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}