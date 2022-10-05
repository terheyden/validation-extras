package com.terheyden.valid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ValidatorsTest unit tests.
 */
public class ValidatorsTest {

    @Test
    public void testCheck() {

        User good = new User("Cora", 10);

        assertThrows(IllegalArgumentException.class, () -> Validators.check(null));
        Validators.check(good);

        assertThrows(IllegalArgumentException.class, () -> Validators.validate(null));
        Validators.validate(good);
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
    }
}
