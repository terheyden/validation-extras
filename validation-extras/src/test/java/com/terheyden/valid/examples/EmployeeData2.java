package com.terheyden.valid.examples;

import java.util.UUID;

import org.slf4j.Logger;

import com.terheyden.valid.MethodValidator;
import com.terheyden.valid.Validations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Demonstrate how to do method parameter validation.
 * Assume this is a singleton class.
 */
public class EmployeeData2 {

    private static final Logger LOG = getLogger(EmployeeData2.class);

    private static final MethodValidator SAVE
        = Validations.createMethodValidator(EmployeeData2.class, "saveEmployee");

    private static final MethodValidator LOAD_UUID
        = Validations.createMethodValidator(EmployeeData2.class, "loadEmployee");

    private static final MethodValidator LOAD_STRING
        = Validations.createMethodValidator(EmployeeData2.class, "loadEmployee");

    public void saveEmployee(@NotNull @Valid Employee employee) {

        SAVE.validateParams(this, employee);
        LOG.info("Saving employee: {}", employee);
    }

    public Employee loadEmployee(@NotNull UUID userId) {

        LOAD_UUID.validateParams(this, userId);
        return new Employee(userId, "Cora", 12);
    }

    public Employee loadEmployee(@NotBlank String userId) {

        LOAD_STRING.validateParams(this, userId);
        return new Employee(UUID.fromString(userId), "Cora", 12);
    }
}
