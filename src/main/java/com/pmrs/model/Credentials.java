// src/main/java/com/pmrs/model/Credentials.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;

public class Credentials {
    private String id; // Usually the username/physician ID
    private String password;
    private Person user;

    public Credentials(String id, String password, Person user) throws ValidationException {
        if (id == null || id.trim().isEmpty()) throw new ValidationException("ID required");
        if (password == null || password.trim().isEmpty()) throw new ValidationException("Password required");
        this.id = id;
        this.password = password;
        this.user = user;
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public Person getUser() { return user; }
}