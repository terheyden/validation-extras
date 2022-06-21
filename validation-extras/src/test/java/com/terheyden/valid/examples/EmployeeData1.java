package com.terheyden.valid.examples;

import java.util.UUID;

import org.slf4j.Logger;

import com.terheyden.valid.Employee;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Demonstrate how to do method parameter validation.
 * Assume this is a singleton class.
 */
public class EmployeeData1 {

    private static final Logger LOG = getLogger(EmployeeData1.class);

    public void saveEmployee(Employee employee) {

        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        if (employee.getName() == null) {
            throw new IllegalArgumentException("Employee name cannot be null.");
        }

        if (employee.getName().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be empty.");
        }

        if (employee.getAge() < 1) {
            throw new IllegalArgumentException("Employee age must be greater than 0.");
        }

        LOG.info("Saving employee: {}", employee);
    }

    public Employee loadEmployee(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        return new Employee(userId, "Cora", 12);
    }

    public Employee loadEmployee(String userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        if (userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }

        return new Employee(UUID.fromString(userId), "Cora", 12);
    }
}
