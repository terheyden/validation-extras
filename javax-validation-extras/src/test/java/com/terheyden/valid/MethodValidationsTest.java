package com.terheyden.valid;

import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.terheyden.valid.ValidationAssertions.assertInvalid;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * MethodValidationsTest unit tests.
 */
public class MethodValidationsTest {

    private static final Logger LOG = getLogger(MethodValidationsTest.class);

    // Validator on a method with no overloads:
    private static final MethodValidator DEBUG
        = Validations.createMethodValidator(MethodValidationsTest.class, "debug");

    // Validator on a method with overloads by param count:
    private static final MethodValidator GREET2
        = Validations.createMethodValidator(MethodValidationsTest.class, "greet", 2);

    @Test
    public void testParams() {

        greet("Howdy", "Cora");
        assertInvalid(() -> greet("X", "Cora"));
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
        DEBUG.validate(this, msg, args);
        LOG.debug(msg, args);
    }

    private String greet(@Length(min = 3) String greeting, @NotBlank String name) {

        GREET2.validate(this, greeting, name);
        return "%s, %s!".formatted(greeting, name);
    }

    private String greet(@NotBlank String name) {
        return greet("Hello", name);
    }
}
