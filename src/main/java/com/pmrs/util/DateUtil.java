// src/main/java/com/pmrs/util/DateUtil.java
package com.pmrs.util;

import java.time.format.DateTimeFormatter;

/**
 * Centralized date formatting definitions to ensure UI consistency.
 */
public class DateUtil {
    public static final DateTimeFormatter STANDARD_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter STANDARD_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
}