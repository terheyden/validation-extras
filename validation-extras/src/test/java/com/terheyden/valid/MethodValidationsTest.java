package com.terheyden.valid;

import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.terheyden.valid.ValidationAssertions.assertInvalid;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * MethodValidationsTest unit tests.
 */
public class MethodValidationsTest {

    private static final Logger LOG = getLogger(MethodValidationsTest.class);

    // Validator on a method with no overloads — simplest way to create a MethodValidator:
    private static final MethodValidator DEBUG
        = Validations.createMethodValidator(MethodValidationsTest.class, "debug");

    // Validator on a method with overloads by param count:
    private static final MethodValidator GREET2
        = Validations.createMethodValidator(MethodValidationsTest.class, "greet", 2);

    // Validator on a method with overloads by unique param type:
    private static final MethodValidator GREET3
        = Validations.createMethodValidator(MethodValidationsTest.class, "greet", Integer.class);

    @Test
    public void testParams() {

        greet("Howdy", "Cora");
        // Greeting is too short:
        assertInvalid(() -> greet("X", "Cora"));
        // Name cannot be null:
        assertInvalid(() -> greet("Howdy", null));

        debug("Testing!");
        assertInvalid(() -> debug(null));
        assertInvalid(() -> debug(""));
    }

    @Test
    public void testBadFinds() {

        // Error! saveEmployee() doesn't have 5 params.
        assertThrows(IllegalStateException.class, () -> Validations
            .createMethodValidator(MethodValidationsTest.class, "saveEmployee", 5));

        // Error! greet() is overloaded so this is ambiguous.
        assertThrows(IllegalStateException.class, () -> Validations
            .createMethodValidator(MethodValidationsTest.class, "greet"));
    }

    private void debug(@NotBlank String msg, @NotNull Object... args) {
        DEBUG.validateParams(this, msg, args);
        LOG.debug(msg, args);
    }

    private String greet(@Length(min = 3) String greeting, @NotBlank String name, int padding) {

        GREET3.validateParams(this, greeting, name, padding);
        return "%s%s, %s!".formatted(" ".repeat(padding), greeting, name);
    }

    private String greet(String greeting, String name) {
        // We don't need method validation here; it's handled by the other greet() method we're calling:
        return greet(greeting, name, 0);
    }

    private String greet(String name) {
        // We don't need method validation here; it's handled by the other greet() method we're calling:
        return greet("Hello", name, 0);
    }
}
