// src/main/java/com/pmrs/model/Admin.java
package com.pmrs.model;

import com.pmrs.exception.ValidationException;
import com.pmrs.model.enums.Gender;
import java.time.LocalDate;

/**
 * Represents a clinic administrator with broad access.
 */
public class Admin extends Person {
    private String department;

    public Admin(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                 String contactNumber, Address address, String department) throws ValidationException {
        super(id, firstName, lastName, dateOfBirth, gender, contactNumber, address);
        setDepartment(department);
    }

    @Override
    public String getRoleDescription() {
        return "System Administrator - " + department;
    }

    @Override
    public String getDashboardView() {
        return "/com/pmrs/view/dashboard.fxml"; // Admin dashboard
    }

    public String getDepartment() { return department; }

    public void setDepartment(String department) throws ValidationException {
        if (department == null || department.trim().isEmpty()) {
            throw new ValidationException("Department cannot be null or empty.");
        }
        this.department = department;
    }
}