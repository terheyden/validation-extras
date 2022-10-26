package com.terheyden.valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import jakarta.validation.ConstraintViolation;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * General bucket for validation-related utility methods that we reuse in multiple classes.
 */
/* package */ final class ValidationUtils {

    private static final Logger LOG = getLogger(ValidationUtils.class);

    /**
     * There is both e.g. a {@code Integer.class} and a {@code int.class} in the Java language.
     * This is a nuance that is not well-documented or well-understood.
     * Since the wrapper class (e.g. {@code Integer.class}) has the most features and is the most commonly used,
     * we use that as the default type to compare against.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.of(
        boolean.class, Boolean.class,
        byte.class, Byte.class,
        char.class, Character.class,
        double.class, Double.class,
        float.class, Float.class,
        int.class, Integer.class
    );

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
        LOG.debug("className: {}, propertyPath: {}, message: {}", className, propertyPath, message);

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
     * Converts {@link ConstraintViolation}s into friendly error message strings.
     * Useful after calling:
     * <ul>
     *     <li>{@link Validations#check(Object)}</li>
     *     <li>{@link MethodValidator#checkParams(Object, Object...)}</li>
     *     <li>{@link ConstructorValidator#checkParams(Object...)}</li>
     * </ul>
     */
    /* package */ static <T> List<String> parseViolationMessages(
        Collection<? extends ConstraintViolation<T>> violations) {

        return violations
            .stream()
            .map(ValidationUtils::createViolationString)
            .toList();
    }

    /**
     * Returns the wrapper class (e.g. {@code Integer.class}) for the given primitive class (e.g. {@code int.class}).
     * @see https://stackoverflow.com/a/22471301
     */
    /* package */ static Class<?> normalizePrimitiveType(Class<?> type) {
        return PRIMITIVE_TO_WRAPPER.getOrDefault(type, type);
    }

    /**
     * Tries to find a method parameter with the given class type, and returns its offset.
     * Performs primitive class normalizing via {@link #normalizePrimitiveType(Class)}
     * to make better matches.
     *
     * @return the offset of the matching parameter, or -1 if not found
     */
    /* package */ static int findParameterByType(Class<?>[] methodParamTypes, Class<?> paramTypeToFind) {

        Class<?> normalizedParamToFind = normalizePrimitiveType(paramTypeToFind);

        for (int offset = 0; offset < methodParamTypes.length; offset++) {

            Class<?> normalizedMethodParam = normalizePrimitiveType(methodParamTypes[offset]);

            if (normalizedMethodParam.equals(normalizedParamToFind)) {
                return offset;
            }
        }

        return -1;
    }
}
