// src/main/java/com/pmrs/model/Address.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;

/**
 * Composed address object representing a physical location.
 */
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;

    /**
     * Constructs a new Address.
     *
     * @param street  The street address.
     * @param city    The city.
     * @param state   The state or province.
     * @param zipCode The postal zip code.
     * @throws ValidationException if any required field is null or blank.
     */
    public Address(String street, String city, String state, String zipCode) throws ValidationException {
        setStreet(street);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
    }

    /**
     * Copy constructor for defensive copying.
     *
     * @param other The address to copy.
     */
    public Address(Address other) {
        this.street = other.street;
        this.city = other.city;
        this.state = other.state;
        this.zipCode = other.zipCode;
    }

    public String getStreet() { return street; }

    public void setStreet(String street) throws ValidationException {
        if (street == null || street.trim().isEmpty()) {
            throw new ValidationException("Street cannot be null or empty.");
        }
        this.street = street;
    }

    public String getCity() { return city; }

    public void setCity(String city) throws ValidationException {
        if (city == null || city.trim().isEmpty()) {
            throw new ValidationException("City cannot be null or empty.");
        }
        this.city = city;
    }

    public String getState() { return state; }

    public void setState(String state) throws ValidationException {
        if (state == null || state.trim().isEmpty()) {
            throw new ValidationException("State cannot be null or empty.");
        }
        this.state = state;
    }

    public String getZipCode() { return zipCode; }

    public void setZipCode(String zipCode) throws ValidationException {
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new ValidationException("Zip code cannot be null or empty.");
        }
        this.zipCode = zipCode;
    }

}
