package com.terheyden.valid;

import javax.annotation.Nullable;
import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import jakarta.validation.ConstraintViolation;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Helper class that handles validation of method and constructor parameters.
 * Used by {@link Valid}.
 */
final class ArgValidation {

    private static final Logger LOG = getLogger(ArgValidation.class);

    private ArgValidation() {
        // Private since this class shouldn't be instantiated.
    }

    static <T> Set<ConstraintViolation<T>> checkMethodArgs(T thisObj, Object[] methodArgs) {
        StackFrame stackFrame = Reflections.getStackFrame(3);
        String methodName = stackFrame.getMethodName();
        return checkMethodArgsInternal(thisObj, methodName, methodArgs);
    }

    private static <T> Set<ConstraintViolation<T>> checkMethodArgsInternal(
        T thisObj,
        String currentMethodName,
        Object[] methodArgs) {

        Class<?> thisObjClass = thisObj.getClass();
        Method currentMethod = findMatchingMethod(thisObjClass, currentMethodName, methodArgs);
        return Valid.EXECUTABLE_VALIDATOR.validateParameters(thisObj, currentMethod, methodArgs);
    }

    static Set<ConstraintViolation<Object>> checkConstructorArgs(Object[] argValues) throws Exception {

        StackFrame stackFrame = Reflections.getStackFrame(3);
        String thisClassName = stackFrame.getClassName();
        Class<?> thisObjClass = Class.forName(thisClassName);

        return checkConstructorArgsInternal(thisObjClass, argValues);
    }

    private static Set<ConstraintViolation<Object>> checkConstructorArgsInternal(
        Class<?> thisObjClass,
        Object[] constructorArgs) {

        Constructor<Object> constructor = findMatchingConstructor(thisObjClass, constructorArgs);
        return Valid.EXECUTABLE_VALIDATOR.validateConstructorParameters(constructor, constructorArgs);
    }

    private static Method findMatchingMethod(Class<?> methodClass, String methodName, Object[] argValues) {
        return findSingularMatchingMethod(methodClass, methodName)
            .or(() -> findSimilarMatchingMethod(methodClass, methodName, argValues))
            .or(() -> findMethodByArgCount(methodClass, methodName, argValues.length))
            .orElseThrow(() -> new IllegalArgumentException("No matching method found."));
    }

    private static Constructor<Object> findMatchingConstructor(Class<?> constructorClass, Object[] argValues) {
        return findSingularMatchingConstructor(constructorClass)
            .or(() -> findSimilarMatchingConstructor(constructorClass, argValues))
            .or(() -> findConstructorByArgCount(constructorClass, argValues.length))
            .orElseThrow(() -> new IllegalArgumentException("No matching constructor found."));
    }

    /**
     * If there is a single method by this name, return it.
     */
    private static Optional<Method> findSingularMatchingMethod(Class<?> methodClass, String methodName) {

        List<Method> matchingMethods = Arrays
            .stream(methodClass.getDeclaredMethods())
            .filter(method -> method.getName().equals(methodName))
            .collect(Collectors.toList());

        return matchingMethods.size() == 1
            ? Optional.of(matchingMethods.get(0))
            : Optional.empty();
    }

    /**
     * If there is a single constructor, return it.
     */
    @SuppressWarnings("unchecked")
    private static Optional<Constructor<Object>> findSingularMatchingConstructor(Class<?> constructorClass) {
        Constructor[] constructors = constructorClass.getDeclaredConstructors();
        return constructors.length == 1
            ? Optional.of((Constructor<Object>) constructors[0])
            : Optional.empty();
    }

    /**
     * Performs a more lenient check than {@link Class#getDeclaredMethod(String, Class[])}.
     * If a param is null, keep going and check the others.
     * If a param is not null, do an instanceof check instead of exact class match.
     */
    private static Optional<Method> findSimilarMatchingMethod(
        Class<?> methodClass,
        String methodName,
        Object[] argValues) {

        return Arrays.stream(methodClass.getDeclaredMethods())
            .filter(method -> method.getName().equals(methodName))
            .filter(method -> isSimilarMethod(method, argValues))
            .findFirst();
    }

    /**
     * Performs a more lenient check than {@link Class#getDeclaredMethod(String, Class[])}.
     * If a param is null, keep going and check the others.
     * If a param is not null, do an instanceof check instead of exact class match.
     */
    @SuppressWarnings("unchecked")
    private static Optional<Constructor<Object>> findSimilarMatchingConstructor(
        Class<?> constructorClass,
        Object[] argValues) {

        return Arrays.stream(constructorClass.getDeclaredConstructors())
            .filter(constructor -> isSimilarConstructor(constructor, argValues))
            .findFirst()
            .map(constructor -> (Constructor<Object>) constructor);
    }

    /**
     * Performs a more lenient check than {@link Class#getDeclaredMethod(String, Class[])}.
     * If a param is null, keep going and check the others.
     * If a param is not null, do an instanceof check instead of exact class match.
     */
    private static boolean isSimilarMethod(Method method, Object[] argValues) {

        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != argValues.length) {
            return false;
        }

        for (int i = 0; i < paramTypes.length; i++) {

            Class<?> paramType = paramTypes[i];
            @Nullable Object paramValue = argValues[i];

            // If the param value is null, we can't tell if it's a match.
            // Keep going and see if the other params match.
            if (paramValue == null) {
                continue;
            }

            // Here we do a more lenient check than findExactMatchingMethod.
            if (!paramType.isAssignableFrom(paramValue.getClass())) {
                // Not a match.
                return false;
            }
        }

        return true;
    }

    /**
     * Performs a more lenient check than {@link Class#getDeclaredMethod(String, Class[])}.
     * If a param is null, keep going and check the others.
     * If a param is not null, do an instanceof check instead of exact class match.
     */
    private static boolean isSimilarConstructor(Constructor<?> constructor, Object[] argValues) {

        Class<?>[] paramTypes = constructor.getParameterTypes();
        if (paramTypes.length != argValues.length) {
            return false;
        }

        for (int i = 0; i < paramTypes.length; i++) {

            Class<?> paramType = paramTypes[i];
            @Nullable Object paramValue = argValues[i];

            // If the param value is null, we can't tell if it's a match.
            // Keep going and see if the other params match.
            if (paramValue == null) {
                continue;
            }

            // Here we do a more lenient check than findExactMatchingMethod.
            if (paramType.isAssignableFrom(paramValue.getClass())) {
                // Not a match.
                return false;
            }
        }

        return true;
    }

    private static Optional<Method> findMethodByArgCount(Class<?> methodClass, String methodName, int argCount) {

        List<Method> matchingMethods = Arrays
            .stream(methodClass.getDeclaredMethods())
            .filter(method -> method.getName().equals(methodName))
            .filter(method -> method.getParameterCount() == argCount)
            .collect(Collectors.toList());

        if (matchingMethods.isEmpty()) {
            LOG.warn("No methods named '{}' with {} arguments found in: {}",
                methodName, argCount, methodClass.getSimpleName());

            return Optional.empty();
        }

        if (matchingMethods.size() > 1) {
            LOG.warn("Multiple methods named '{}' with {} arguments found in: {}",
                methodName, argCount, methodClass.getSimpleName());

            return Optional.empty();
        }

        return Optional.of(matchingMethods.get(0));
    }

    @SuppressWarnings("unchecked")
    private static Optional<Constructor<Object>> findConstructorByArgCount(
        Class<?> constructorClass,
        int argCount) {

        List<Constructor<?>> matchingConstructors = Arrays
            .stream(constructorClass.getDeclaredConstructors())
            .filter(constructor -> constructor.getParameterCount() == argCount)
            .collect(Collectors.toList());

        if (matchingConstructors.isEmpty()) {
            LOG.warn("No constructor with {} arguments found in: {}",
                argCount, constructorClass.getSimpleName());

            return Optional.empty();
        }

        if (matchingConstructors.size() > 1) {
            LOG.warn("Multiple constructors with {} arguments found in: {}",
                argCount, constructorClass.getSimpleName());

            return Optional.empty();
        }

        return Optional.of((Constructor<Object>) matchingConstructors.get(0));
    }
}
