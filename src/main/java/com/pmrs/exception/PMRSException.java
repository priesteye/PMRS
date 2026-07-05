// src/main/java/com/pmrs/exception/PMRSException.java
package com.pmrs.exception;

/**
 * The base checked exception for the Patient Medical Record System.
 * All domain and service layer exceptions must inherit from this class
 * to ensure controllers handle them explicitly at the boundary.
 */
public class PMRSException extends Exception {

    public PMRSException(String message) {
        super(message);
    }

    public PMRSException(String message, Throwable cause) {
        super(message, cause);
    }
}