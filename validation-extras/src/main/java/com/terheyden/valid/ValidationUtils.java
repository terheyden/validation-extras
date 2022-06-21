package com.terheyden.valid;

import java.util.Collection;

import jakarta.validation.ConstraintViolation;

/**
 * General bucket for validation-related utility methods that we reuse in multiple classes.
 */
/* package */ final class ValidationUtils {

    private ValidationUtils() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Throw any exception unchecked.
     */
    @SuppressWarnings("unchecked")
    /* package */ static <E extends Throwable, R> R throwUnchecked(Throwable throwable) throws E {
        throw (E) throwable;
    }

    /**
     * Create a human-readable error message from a constraint violation.
     */
    /* package */ static String createViolationString(ConstraintViolation<?> violation) {

        String className = violation.getRootBeanClass().getSimpleName();
        String propertyPath = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        // The property path will be an empty string, if the violation is on the class itself.
        if (propertyPath.isEmpty()) {
            return "%s: %s".formatted(className, message);
        } else {
            return "%s.%s: %s".formatted(className, propertyPath, message);
        }
    }

    /**
     * Return a single human-readable error string for the given constraint violations.
     */
    /* package */ static String createViolationString(Collection<? extends ConstraintViolation<?>> violations) {

        if (violations == null || violations.isEmpty()) {
            return "";
        }

        return String.join("; ", violations.stream()
            .map(ValidationUtils::createViolationString)
            .toList());
    }

    /**
     * Tries to find a method parameter with the given class type, and returns its offset.
     *
     * @return the offset of the matching parameter, or -1 if not found
     */
    /* package */ static int findParameterByType(Class<?>[] methodParamTypes, Class<?> paramTypeToFind) {

        for (int offset = 0; offset < methodParamTypes.length; offset++) {
            if (methodParamTypes[offset].equals(paramTypeToFind)) {
                return offset;
            }
        }

        return -1;
    }
}
