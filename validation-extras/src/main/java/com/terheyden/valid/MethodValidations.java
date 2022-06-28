package com.terheyden.valid;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Helper class just for validating method parameters.
 * Package-protected, used solely by {@link Validations}.
 */
/* package */ final class MethodValidations {

    private MethodValidations() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Find a single method of a class, for validating its parameters.
     * <p>
     * This version finds a {@link Method} with a certain name.
     * This should only be used when there are no method overloads.
     * Check out the other {@code findMethod()} methods for other uses.
     */
    /* package */ static Method findMethod(Class<?> classType, String methodName) {
        try {

            Method foundMethod = null;

            for (Method method : classType.getDeclaredMethods()) {

                if (!method.getName().equals(methodName)) {
                    // Not the right method name, keep looking.
                    continue;
                }

                // If more than one match, warn the user.
                if (foundMethod != null) {
                    throw new IllegalStateException(
                        "Ambiguous; more than one method found with name '%s' in class '%s'."
                        .formatted(methodName, classType.getName()));
                }

                // Disqualify static methods.
                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                // It's the first match.
                foundMethod = method;
            }

            if (foundMethod == null) {
                throw new IllegalStateException(
                    "No (non-static) method found with name '%s' in class '%s'."
                    .formatted(methodName, classType.getName()));
            } else {
                // All methods checked, only one match found, so return it.
                return foundMethod;
            }

        } catch (SecurityException e) {
            return ValidationUtils.throwUnchecked(e);
        }
    }

    /**
     * Find a single method of a class, for validating its parameters.
     * <p>
     * This version finds a {@link Method} with the given name and unique parameter type.
     * For example, if you had {@code myMethod(String, String)}, and
     * @code myMethod(String, Integer)}, calling {@code findMethod(MyClass.class, "myMethod", Integer.class)}
     * would return the second method (because it is the version with an {@code Integer} parameter.
     * <p>
     * Check out the other {@code findMethod()} methods for other uses.
     */
    /* package */ static Method findMethod(Class<?> classType, String methodName, Class<?> anyParamType) {
        try {

            Method foundMethod = null;

            for (Method method : classType.getDeclaredMethods()) {

                if (!method.getName().equals(methodName)) {
                    // Not the right method name, keep looking.
                    continue;
                }

                Class<?>[] methodParameterTypes = method.getParameterTypes();

                if (methodParameterTypes.length == 0) {
                    // Looking for at least 1 param.
                    continue;
                }

                if (ValidationUtils.findParameterByType(methodParameterTypes, anyParamType) == -1) {
                    // No param of the right type, keep looking.
                    continue;
                }

                // If more than one match, warn the user.
                if (foundMethod != null) {
                    throw new IllegalStateException(
                        "Ambiguous; more than one method found with name '%s' in class '%s' and parameter type '%s'."
                        .formatted(methodName, classType.getName(), anyParamType));
                }

                // Disqualify static methods.
                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                // It's the first match.
                foundMethod = method;
            }

            if (foundMethod == null) {
                throw new IllegalStateException(
                    "No (non-static) method found with name '%s' in class '%s' and parameter type '%s'."
                    .formatted(methodName, classType.getName(), anyParamType));
            } else {
                // All methods checked, only one match found, so return it.
                return foundMethod;
            }

        } catch (SecurityException e) {
            return ValidationUtils.throwUnchecked(e);
        }
    }

    /**
     * Find a single method of a class, for validating its parameters.
     * <p>
     * This version finds a {@link Method} with the given name and given number of parameters.
     * For example, if you had {@code myMethod(String, String)}, and
     * @code myMethod(String)}, calling {@code findMethod(MyClass.class, "myMethod", 1)}
     * would return the second method.
     * <p>
     * Check out the other {@code findMethod()} methods for other uses.
     */
    /* package */ static Method findMethod(Class<?> classType, String methodName, int paramCount) {
        try {

            Method foundMethod = null;

            for (Method method : classType.getDeclaredMethods()) {

                if (!method.getName().equals(methodName)) {
                    // Not the right method name, keep looking.
                    continue;
                }

                if (method.getParameterTypes().length != paramCount) {
                    // Not the right number of params, keep looking.
                    continue;
                }

                // If more than one match, warn the user.
                if (foundMethod != null) {
                    throw new IllegalStateException(
                        "Ambiguous; more than one method found with name '%s' in class '%s' and %d parameters."
                        .formatted(methodName, classType.getName(), paramCount));
                }

                // Disqualify static methods.
                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                // It's the first match.
                foundMethod = method;
            }

            if (foundMethod == null) {
                throw new IllegalStateException(
                    "No (non-static) method found with name '%s' in class '%s' and %d parameters."
                    .formatted(methodName, classType.getName(), paramCount));
            } else {
                // All methods checked, only one match found, so return it.
                return foundMethod;
            }

        } catch (SecurityException e) {
            return ValidationUtils.throwUnchecked(e);
        }
    }
}
