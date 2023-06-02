package com.terheyden.valid;

import java.util.Set;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ValidationsTest unit tests.
 */
public class ValidationsTest {

    @Test
    public void testGetterChecks() {

        User1 good = new User1("Cora", 10);

        assertTrue(Validations.checkToList(good).isEmpty());
        Validations.validate(good);

        // Verify that the var that comes back from check() actually works with parse().
        User1 badUser = new User1("x", -1);
        Set<ConstraintViolation<User1>> violations = Validations.check(badUser);
        assertFalse(violations.isEmpty());

        User1 nullUser = new User1(null, -1);
        violations = Validations.check(nullUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullObj() {

        assertThrows(NullPointerException.class, () -> Validations.validate(null));

        String violations = Validations.checkToString(null);
        assertEquals("NullOriginViolation: Object to validate is null.", violations);
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
    private static class User1 {
        private final String name;
        private final int age;

        private User1(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @NotNull
        @Size(min = 2)
        public String getName() {
            return name;
        }

        @Min(1)
        public int getAge() {
            return age;
        }
    }

    /**
     * This class is self-validating.
     */
    private static class User2 {
        private final String name;
        private final int age;

        private User2(String name, int age) {
            this.name = name;
            this.age = age;
            validateSelf();
        }

        private void validateSelf() {
            Validations.validate(this);
        }

        @NotNull
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
