package com.terheyden.valid.examples;

import com.terheyden.valid.Validations;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Example of a self-validating object
 * without the need for method validators.
 */
public class ValidCat {

    @Size(min = 3)
    private final String name;

    @Min(1)
    private final int age;

    private ValidCat(String name, int age) {
        this.name = name;
        this.age = age;
        // Validations.validate(this); // "Escape of 'this' during obj construction."
    }

    /**
     * By using a static factory method (or a builder), we can be self-validating.
     */
    public static ValidCat newValidCat(String name, int age) {
        return Validations.validate(new ValidCat(name, age));
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
