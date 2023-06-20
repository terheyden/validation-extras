package com.terheyden.valid;

import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ValidationsTest unit tests.
 */
class ValidationsTest {

    @Test
    void testGetterChecks() {

        User1 good = new User1("Cora", 10);

        assertTrue(Validations.checkObject(good).isEmpty());
        Validations.validateObject(good);

        // Verify that the var that comes back from checkObject() actually works with parse().
        User1 badUser = new User1("x", -1);
        Set<ConstraintViolation<User1>> violations = Validations.checkObject(badUser);
        assertFalse(violations.isEmpty());

        User1 nullUser = new User1(null, -1);
        violations = Validations.checkObject(nullUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullObj() {

        assertThrows(NullPointerException.class, () -> Validations.validateObject(null));

        Set<ConstraintViolation<Object>> violations = Validations.checkObject(null);
        assertEquals("Object to validate is null.", violations.iterator().next().getMessage());
    }

    @Test
    void testSelfValidation() {

        // Self-validate should succeed.
        User2 goodUser = new User2("Cora", 10);

        assertThrows(ConstraintViolationException.class, () -> new User2("x", -1));
    }

    /**
     * For testing validation.
     */
    private static class User1 implements BaseUser {
        private final String name;
        private final int age;

        private User1(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @NotNull
        @Size(min = 2)
        @Override
        public String getName() {
            return name;
        }

        @Min(1)
        @Override
        public int getAge() {
            return age;
        }
    }

    /**
     * This class is self-validating.
     */
    private static class User2 implements BaseUser {
        private final String name;
        private final int age;

        private User2(
            @NotNull @Size(min = 2) String name,
            @Min(3) int age) {

            Validations.validateConstructorArgs(name, age);
            this.name = name;
            this.age = age;
        }

        @NotNull
        @Size(min = 2)
        @Override
        public String getName() {
            return name;
        }

        @Min(1)
        @Override
        public int getAge() {
            return age;
        }
    }

    private interface BaseUser {
        String getName();
        int getAge();
    }
}
