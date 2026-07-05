// src/main/java/com/pmrs/model/Person.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;
import com.pmrs.model.enums.Gender;
import java.time.LocalDate;

/**
 * Abstract base class representing any human user or subject in the PMRS system.
 */
public abstract class Person {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String contactNumber;
    private Address address;

    /**
     * Base constructor for Person.
     *
     * @param id            System-generated unique identifier.
     * @param firstName     First name.
     * @param lastName      Last name.
     * @param dateOfBirth   Date of birth (cannot be in the future).
     * @param gender        Gender enum.
     * @param contactNumber Contact phone number.
     * @param address       Address object.
     * @throws ValidationException if validation rules are violated.
     */
    public Person(String id, String firstName, String lastName, LocalDate dateOfBirth,
                  Gender gender, String contactNumber, Address address) throws ValidationException {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setDateOfBirth(dateOfBirth);
        setGender(gender);
        setContactNumber(contactNumber);
        setAddress(address);
    }

    /**
     * @return A description of the specific role for UI display.
     */
    public abstract String getRoleDescription();

    /**
     * @return The FXML view path this role should be routed to upon login.
     */
    public abstract String getDashboardView();

    public String getId() { return id; }

    public void setId(String id) throws ValidationException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("ID cannot be null or empty.");
        }
        this.id = id;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) throws ValidationException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("First name cannot be null or empty.");
        }
        this.firstName = firstName;
    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) throws ValidationException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Last name cannot be null or empty.");
        }
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() { return dateOfBirth; } // LocalDate is immutable

    public void setDateOfBirth(LocalDate dateOfBirth) throws ValidationException {
        if (dateOfBirth == null) {
            throw new ValidationException("Date of birth cannot be null.");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future.");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() { return gender; }

    public void setGender(Gender gender) throws ValidationException {
        if (gender == null) {
            throw new ValidationException("Gender cannot be null.");
        }
        this.gender = gender;
    }

    public String getContactNumber() { return contactNumber; }

    public void setContactNumber(String contactNumber) throws ValidationException {
        if (contactNumber == null || !contactNumber.matches("^\\+?[0-9\\-\\s]{7,15}$")) {
            throw new ValidationException("Contact number is invalid or empty.");
        }
        this.contactNumber = contactNumber;
    }

    public Address getAddress() {
        return address != null ? new Address(address) : null;
    }

    public void setAddress(Address address) throws ValidationException {
        if (address == null) {
            throw new ValidationException("Address cannot be null.");
        }
        this.address = new Address(address); // Store defensive copy
    }
}