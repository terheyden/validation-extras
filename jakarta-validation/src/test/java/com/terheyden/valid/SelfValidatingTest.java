package com.terheyden.valid;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * SelfValidatingTest unit tests.
 */
public class SelfValidatingTest {

    @Test
    public void test() {

        User goodUser = new User("Cora", 10, true, "cora@catmail.com");

        assertThrows(ConstraintViolationException.class, () -> new User("x", -1, false, "meow?"));
    }

    /**
     * Let's test.
     */
    private static final class User implements SelfValidating {

        @NotBlank(message = "Name is required.")
        @Size(min = 3)
        @Pattern(
            regexp = "[a-zA-Z]+",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Name must be letters only.")
        private final String name;

        @Min(1)
        private final int age;

        @AssertTrue
        private final boolean enabled;

        @Email
        private final String email;

        private User(String name, int age, boolean enabled, String email) {
            this.name = name;
            this.age = age;
            this.enabled = enabled;
            this.email = email;
            validateSelf();
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getEmail() {
            return email;
        }
    }
}
