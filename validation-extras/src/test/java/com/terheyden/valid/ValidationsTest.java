package com.terheyden.valid;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ValidationsTest unit tests.
 */
public class ValidationsTest {

    @Test
    public void testCheck() {

        User good = new User("Cora", 10);

        assertThrows(IllegalArgumentException.class, () -> Validations.check(null));
        Validations.check(good);

        assertThrows(IllegalArgumentException.class, () -> Validations.validate(null));
        Validations.validate(good);

        // Verify that the var that comes back from check() actually works with parse().
        Set<ConstraintViolation<User>> violations = Validations.check(new User("x", -1));
        assertFalse(violations.isEmpty());
        List<String> violationMessages = Validations.parseViolationMessages(violations);
        assertEquals(violations.size(), violationMessages.size());
    }

    /**
     * Trivial user class for testing.
     */
    private static class User {
        private final String name;
        private final int age;

        private User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Size(min = 2)
        public String getName() {
            return name;
        }

        @Min(1)
        public int getAge() {
            return age;
        }
    }
}
