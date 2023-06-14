package com.terheyden.valid;

import javax.annotation.Nullable;
import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * ParamValidation class.
 */
final class ParamValidation {

    private static final Logger LOG = getLogger(ParamValidation.class);

    private ParamValidation() {
        // Private since this class shouldn't be instantiated.
    }

    private static <T> Set<ConstraintViolation<T>> checkMethodParamsInternal(
        T thisObj,
        int stackDepth,
        Object[] methodParams) {

        StackFrame currentStackFrame = Reflections.getCurrentStackFrame(stackDepth);
        String currentMethodName = currentStackFrame.getMethodName();
        Class<?> thisObjClass = thisObj.getClass();
        Method currentMethod = findMatchingMethod(thisObjClass, currentMethodName, methodParams);

        return Validations.EXECUTABLE_VALIDATOR.validateParameters(
            thisObj, currentMethod, methodParams);
    }

    private static <T> Set<ConstraintViolation<T>> checkConstructorParamsInternal(
        T thisObj,
        Object[] constructorParams) {

        Class<T> thisObjClass = (Class<T>) thisObj.getClass();
        Constructor<T> constructor = (Constructor<T>) findMatchingConstructor(thisObjClass, constructorParams);

        return Validations.EXECUTABLE_VALIDATOR.validateConstructorParameters(constructor, constructorParams);
    }

    static <T> Set<ConstraintViolation<T>> checkMethodParams(T thisObj, Object... methodParams) {
        return checkMethodParamsInternal(thisObj, 3, methodParams);
    }

    static List<String> checkMethodParamsToList(Object thisObj, Object... methodParams) {
        return checkMethodParamsInternal(thisObj, 3, methodParams)
            .stream()
            .map(Validations::violationToString)
            .collect(Collectors.toList());
    }

    static String checkMethodParamsToString(Object thisObj, Object... methodParams) {
        return Validations.violationsToString(checkMethodParamsInternal(thisObj, 3, methodParams));
    }

    /**
     * Validate the parameters of the current method using Jakarta Bean Validation annotations.
     * @param thisObj should literally be {@code this}
     * @param methodParams all parameters passed to the current method, in proper order
     * @return {@code this}, for chaining
     */
    static <T> T validateMethodParams(T thisObj, Object... methodParams) {

        Set<ConstraintViolation<T>> violations = checkMethodParamsInternal(thisObj, 3, methodParams);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return thisObj;
    }

    /**
     * Validate the parameters of the current method using Jakarta Bean Validation annotations.
     * @param thisObj should literally be {@code this}
     * @param methodParams all parameters passed to the current method, in proper order
     * @return {@code this}, for chaining
     */
    static <T> T validateConstructorParams(T thisObj, Object... methodParams) {

        Set<ConstraintViolation<T>> violations = checkConstructorParamsInternal(thisObj, methodParams);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return thisObj;
    }

    static Method findMatchingMethod(Class<?> methodClass, String methodName, Object[] paramValues) {
        return findSingularMatchingMethod(methodClass, methodName)
            .or(() -> findSimilarMatchingMethod(methodClass, methodName, paramValues))
            .or(() -> findMethodByParamCount(methodClass, methodName, paramValues.length))
            .orElseThrow(() -> new IllegalArgumentException("No matching method found."));
    }

    static Constructor findMatchingConstructor(Class<?> constructorClass, Object[] paramValues) {
        return findSingularMatchingConstructor(constructorClass)
            .or(() -> findSimilarMatchingConstructor(constructorClass, paramValues))
            .or(() -> findConstructorByParamCount(constructorClass, paramValues.length))
            .orElseThrow(() -> new IllegalArgumentException("No matching constructor found."));
    }

    /**
     * If there is a single method by this name, return it.
     */
    static Optional<Method> findSingularMatchingMethod(Class<?> methodClass, String methodName) {

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
    static Optional<Constructor> findSingularMatchingConstructor(Class<?> constructorClass) {
        Constructor<?>[] constructors = constructorClass.getDeclaredConstructors();
        return constructors.length == 1
            ? Optional.of(constructors[0])
            : Optional.empty();
    }

    /**
     * Try to find an exact match based on param class types.
     * Returns Optional instead of throwing.
     */
    static Optional<Method> findExactMatchingMethod(Class<?> methodClass, String methodName, Object[] paramValues) {

        // If any of the values are null, we can't find with an exact match.
        if (Arrays.stream(paramValues).anyMatch(Objects::isNull)) {
            return Optional.empty();
        }

        Class<?>[] paramTypes = Arrays.stream(paramValues).map(Object::getClass).toArray(Class<?>[]::new);

        try {

            return Optional.of(methodClass.getDeclaredMethod(methodName, paramTypes));

        } catch (Exception e) {
            LOG.error("Error finding exact matching method '{}' in '{}' with parameters: {}",
                methodName, methodClass.getSimpleName(), Arrays.asList(paramTypes), e);
            return Optional.empty();
        }
    }

    /**
     * Performs a more lenient check than {@link Class#getDeclaredMethod(String, Class[])}.
     * If a param is null, keep going and check the others.
     * If a param is not null, do an instanceof check instead of exact class match.
     */
    static Optional<Method> findSimilarMatchingMethod(Class<?> methodClass, String methodName, Object[] paramValues) {
        return Arrays.stream(methodClass.getDeclaredMethods())
            .filter(method -> method.getName().equals(methodName))
            .filter(method -> isSimilarMethod(method, paramValues))
            .findFirst();
    }

    /**
     * Performs a more lenient check than {@link Class#getDeclaredMethod(String, Class[])}.
     * If a param is null, keep going and check the others.
     * If a param is not null, do an instanceof check instead of exact class match.
     */
    static Optional<Constructor> findSimilarMatchingConstructor(Class<?> constructorClass, Object[] paramValues) {
        return Arrays.stream(constructorClass.getDeclaredConstructors())
            .filter(constructor -> isSimilarConstructor(constructor, paramValues))
            .map(Constructor.class::cast)
            .findFirst();
    }

    /**
     * Performs a more lenient check than {@link Class#getDeclaredMethod(String, Class[])}.
     * If a param is null, keep going and check the others.
     * If a param is not null, do an instanceof check instead of exact class match.
     */
    static boolean isSimilarMethod(Method method, Object[] paramValues) {

        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != paramValues.length) {
            return false;
        }

        for (int i = 0; i < paramTypes.length; i++) {

            Class<?> paramType = paramTypes[i];
            @Nullable Object paramValue = paramValues[i];

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
    static boolean isSimilarConstructor(Constructor constructor, Object[] paramValues) {

        Class<?>[] paramTypes = constructor.getParameterTypes();
        if (paramTypes.length != paramValues.length) {
            return false;
        }

        for (int i = 0; i < paramTypes.length; i++) {

            Class<?> paramType = paramTypes[i];
            @Nullable Object paramValue = paramValues[i];

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

    /**
     * Try to find an exact match based on param class types.
     * Returns Optional instead of throwing.
     */
    static Optional<Constructor> findExactMatchingConstructor(
        Class<?> constructorClass,
        Object[] paramValues) {

        // If any of the values are null, we can't find with an exact match.
        if (Arrays.stream(paramValues).anyMatch(Objects::isNull)) {
            return Optional.empty();
        }

        Class<?>[] paramTypes = Arrays.stream(paramValues).map(Object::getClass).toArray(Class<?>[]::new);

        try {

            return Optional.of(constructorClass.getDeclaredConstructor(paramTypes));

        } catch (Exception e) {
            LOG.error("Error finding exact matching constructor in '{}' with parameters: {}",
                constructorClass.getSimpleName(), Arrays.asList(paramTypes), e);
            return Optional.empty();
        }
    }

    static Optional<Method> findMethodByParamCount(Class<?> methodClass, String methodName, int paramCount) {

        List<Method> matchingMethods = Arrays
            .stream(methodClass.getDeclaredMethods())
            .filter(method -> method.getName().equals(methodName))
            .filter(method -> method.getParameterCount() == paramCount)
            .collect(Collectors.toList());

        if (matchingMethods.isEmpty()) {
            LOG.warn("No methods named '{}' with {} params found in: {}",
                methodName, paramCount, methodClass.getSimpleName());

            return Optional.empty();
        }

        if (matchingMethods.size() > 1) {
            LOG.warn("Multiple methods named '{}' with {} params found in: {}",
                methodName, paramCount, methodClass.getSimpleName());

            return Optional.empty();
        }

        return Optional.of(matchingMethods.get(0));
    }

    static Optional<Constructor> findConstructorByParamCount(Class<?> constructorClass, int paramCount) {

        List<Constructor> matchingConstructors = Arrays
            .stream(constructorClass.getDeclaredConstructors())
            .filter(constructor -> constructor.getParameterCount() == paramCount)
            .collect(Collectors.toList());

        if (matchingConstructors.isEmpty()) {
            LOG.warn("No constructor with {} params found in: {}",
                paramCount, constructorClass.getSimpleName());

            return Optional.empty();
        }

        if (matchingConstructors.size() > 1) {
            LOG.warn("Multiple constructors with {} params found in: {}",
                paramCount, constructorClass.getSimpleName());

            return Optional.empty();
        }

        return Optional.of(matchingConstructors.get(0));
    }
}
