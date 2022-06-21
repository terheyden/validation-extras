package com.terheyden.valid;

import java.lang.reflect.Constructor;

/**
 * Helper class just for validating constructor parameters.
 * Package-protected, used solely by {@link Validations}.
 */
/* package */ final class ConstructorValidations {

    private ConstructorValidations() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Find and return the one constructor of a class, for validating its parameters.
     * Check out the other {@code findConstructor()} methods for other ways of finding the right constructor.
     *
     * @throws IllegalStateException if more than one matching constructor is found
     */
    /* package */ static Constructor<?> findConstructor(Class<?> classType) {
        try {

            Constructor<?>[] constructors = classType.getDeclaredConstructors();

            if (constructors.length == 0) {
                throw new IllegalStateException("No constructors found in class '%s'."
                    .formatted(classType.getSimpleName()));
            }

            if (constructors.length > 1) {
                throw new IllegalStateException(
                    "Ambiguous; more than one constructor found in class '%s'."
                    .formatted(classType.getSimpleName()));
            }

            // All constructors checked, only one match found, so return it.
            return constructors[0];

        } catch (SecurityException e) {
            return ValidationUtils.throwUnchecked(e);
        }
    }

    /**
     * Find a single constructor of a class, for validating its parameters.
     * <p>
     * This version finds a {@link Constructor} with the given name and unique parameter type.
     * For example, if you had {@code MyClass(String, String)}, and
     * @code MyClass(String, Integer)}, calling {@code findConstructor(MyClass.class, Integer.class)}
     * would return the second constructor (because it is the version with an {@code Integer} parameter.
     * <p>
     * Check out the other {@code findConstructor()} method for other ways of finding the right constructor.
     */
    /* package */ static Constructor<?> findConstructor(Class<?> classType, Class<?> anyParamType) {
        try {

            Constructor<?> foundConstructor = null;

            for (Constructor<?> constructor : classType.getDeclaredConstructors()) {

                Class<?>[] constructorParameterTypes = constructor.getParameterTypes();

                if (constructorParameterTypes.length == 0) {
                    // Looking for at least 1 param.
                    continue;
                }

                if (ValidationUtils.findParameterByType(constructorParameterTypes, anyParamType) == -1) {
                    // No param of the right type, keep looking.
                    continue;
                }

                // If more than one match, warn the user.
                if (foundConstructor != null) {
                    throw new IllegalStateException(
                        "Ambiguous; more than one constructor found with parameter type '%s' in class '%s'."
                        .formatted(anyParamType.getName(), classType.getName()));
                }

                // It's the first match, save it.
                foundConstructor = constructor;
            }

            if (foundConstructor == null) {
                throw new IllegalStateException(
                    "No constructor found with parameter type '%s' in class '%s'."
                    .formatted(anyParamType.getName(), classType.getName()));
            } else {
                // All constructors checked, only one match found, so return it.
                return foundConstructor;
            }

        } catch (SecurityException e) {
            return ValidationUtils.throwUnchecked(e);
        }
    }

    /**
     * Find a single constructor of a class, for validating its parameters.
     * <p>
     * This version finds a {@link Constructor} with the given name and the given number of parameters.
     * For example, if you had {@code MyClass(String, String)}, and
     * @code MyClass(String)}, calling {@code findConstructor(MyClass.class, 1)}
     * would return the second constructor.
     * <p>
     * Check out the other {@code findConstructor()} method for other ways of finding the right constructor.
     */
    /* package */ static Constructor<?> findConstructor(Class<?> classType, int paramCount) {
        try {

            Constructor<?> foundConstructor = null;

            for (Constructor<?> constructor : classType.getDeclaredConstructors()) {

                if (constructor.getParameterTypes().length != paramCount) {
                    // Not the right number of params, keep looking.
                    continue;
                }

                // If more than one match, warn the user.
                if (foundConstructor != null) {
                    throw new IllegalStateException(
                        "Ambiguous; more than one constructor found with %d parameters in class '%s'."
                        .formatted(paramCount, classType.getName()));
                }

                // It's the first match.
                foundConstructor = constructor;
            }

            if (foundConstructor == null) {
                throw new IllegalStateException(
                    "No constructor found with %d parameters in class '%s'."
                    .formatted(paramCount, classType.getName()));
            } else {
                // All constructors checked, only one match found, so return it.
                return foundConstructor;
            }

        } catch (SecurityException e) {
            return ValidationUtils.throwUnchecked(e);
        }
    }
}
