package com.terheyden.valid.examples;

import java.util.UUID;

import com.terheyden.valid.ConstructorValidator;
import com.terheyden.valid.Validations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * An example of how to use Jakarta Bean Validation and the validation-extras library.
 */
@ValidEmployee
public class Employee {

    /**
     * Validator for our constructor with 3 params.
     * 'EMPLOYEE' or 'EMPLOYEE3' are good constructor validator names.
     */
    private static final ConstructorValidator EMPLOYEE = Validations.createConstructorValidator(Employee.class, 3);

    @NotNull
    private final UUID userId;

    @NotBlank
    private final String name;

    @Min(1)
    private final int age;

    public Employee(@NotNull UUID userId, @NotBlank String name, @Min(1) int age) {

        EMPLOYEE.validateParams(userId, name, age);
        this.userId = userId;
        this.name = name;
        this.age = age;
    }

    public Employee(@NotBlank String name, @Min(1) int age) {
        this(UUID.randomUUID(), name, age);
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
