package com.terheyden.valid;

import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Employee class.
 */
public class Employee {

    /**
     * Validator for our constructor with 2 params.
     */
    private static final ConstructorValidator EMPLOYEE2 = Validations.createConstructorValidator(Employee.class, 2);

    /**
     * Validator for our constructor with 3 params.
     */
    private static final ConstructorValidator EMPLOYEE3 = Validations.createConstructorValidator(Employee.class, 3);

    @NotNull
    private final UUID userId;

    @NotBlank
    private final String name;

    @Min(1)
    private final int age;

    public Employee(@NotNull UUID userId, @NotBlank String name, @Min(1) int age) {

        EMPLOYEE3.validate(userId, name, age);
        this.userId = userId;
        this.name = name;
        this.age = age;
    }

    public Employee(@NotBlank String name, @Min(1) int age) {

        EMPLOYEE2.validate(name, age);
        this.userId = UUID.randomUUID();
        this.name = name;
        this.age = age;
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
