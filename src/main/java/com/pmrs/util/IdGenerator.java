// src/main/java/com/pmrs/util/IdGenerator.java
package com.pmrs.util;

import java.util.UUID;

/**
 * Utility for generating standardized, collision-free system IDs.
 */
public class IdGenerator {

    /**
     * Generates a unique identifier with a domain-specific prefix.
     * @param prefix The entity prefix (e.g., "PT" for Patient, "AP" for Appointment).
     * @return A formatted ID string (e.g., PT-123e4567).
     */
    public static String generateId(String prefix) {
        // Using the first 8 characters of a UUID for human-readable but collision-resistant IDs
        String shortUuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + shortUuid;
    }
}