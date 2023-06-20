package com.terheyden.valid;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import static java.lang.String.format;

/**
 * Jakarta Bean Valiation utils methods.
 */
public final class ValidUtils {

    static final String EMPTY_STR = "";

    private ValidUtils() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Create a human-readable error message from a constraint violation.
     */
    public static String violationToString(@Nullable ConstraintViolation<?> violation) {

        if (violation == null) {
            return EMPTY_STR;
        }

        String className = violation.getRootBeanClass().getSimpleName();
        String propertyPath = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        // The property path will be an empty string, if the violation is on the class itself.
        return propertyPath.isEmpty()
            ? format("%s: %s", className, message)
            : format("%s.%s: %s", className, propertyPath, message);
    }

    /**
     * Return a single human-readable error string for the given constraint violations.
     */
    public static String violationsToString(@Nullable Collection<? extends ConstraintViolation<?>> violations) {

        if (violations == null || violations.isEmpty()) {
            return EMPTY_STR;
        }

        return violations.stream()
            .map(ValidUtils::violationToString)
            .collect(Collectors.joining("; "));
    }

    /**
     * Throw any exception unchecked.
     */
    @SuppressWarnings("unchecked")
    static <E extends Throwable, R> R throwUnchecked(Throwable throwable) throws E {
        throw (E) throwable;
    }
}
