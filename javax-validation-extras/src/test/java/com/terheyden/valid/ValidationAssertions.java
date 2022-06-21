package com.terheyden.valid;

import org.junit.jupiter.api.function.Executable;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Validation-specific assertions for our tests.
 */
public final class ValidationAssertions {

    private ValidationAssertions() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Calls {@link Validations#validate(Object)} and asserts that it throws a {@link ValidationException}.
     */
    public static void assertInvalid(Object invalidObj) {
        assertThrows(ValidationException.class, () -> Validations.validate(invalidObj));
    }

    /**
     * Asserts that the given {@link Executable} throws a {@link ValidationException} when run.
     */
    public static void assertInvalid(Executable invalidObjSupplier) {
        assertThrows(ValidationException.class, invalidObjSupplier);
    }

    /**
     * Calls {@link Validations#validate(Object)} and asserts that it doesn't throw a {@link ValidationException}.
     */
    public static void assertValid(Object validObj) {
        Validations.validate(validObj);
    }
}
