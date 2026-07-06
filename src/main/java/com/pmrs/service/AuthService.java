// src/main/java/com/pmrs/service/AuthService.java
package com.pmrs.service;

import com.pmrs.exception.RecordNotFoundException;
import com.pmrs.exception.ValidationException;
import com.pmrs.model.Credentials;
import com.pmrs.model.Person;
import com.pmrs.repository.Repository;

public class AuthService {
    private final Repository<Credentials> credentialRepository;

    public AuthService(Repository<Credentials> credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    public Person authenticate(String username, String password) throws ValidationException {
        try {
            Credentials creds = credentialRepository.findById(username);
            if (!creds.getPassword().equals(password)) {
                throw new ValidationException("Invalid password.");
            }
            return creds.getUser();
        } catch (RecordNotFoundException e) {
            throw new ValidationException("User ID not found in system.");
        }
    }
}