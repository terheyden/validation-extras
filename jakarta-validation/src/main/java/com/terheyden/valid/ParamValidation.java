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
 * ParamValidation class.
 */
final class ParamValidation {

    private static final Logger LOG = getLogger(ParamValidation.class);

    private ParamValidation() {
        // Private since this class shouldn't be instantiated.
    }

    static <T> Set<ConstraintViolation<T>> checkParameters(T thisObj, Object[] methodParams) {

        StackFrame stackFrame = Reflections.getCurrentStackFrame(3);
        String methodName = stackFrame.getMethodName();

        return checkMethodParamsInternal(thisObj, methodName, methodParams);
    }

    private static <T> Set<ConstraintViolation<T>> checkMethodParamsInternal(
        T thisObj,
        String currentMethodName,
        Object[] methodParams) {

        Class<?> thisObjClass = thisObj.getClass();
        Method currentMethod = findMatchingMethod(thisObjClass, currentMethodName, methodParams);
        return Validations.EXECUTABLE_VALIDATOR.validateParameters(thisObj, currentMethod, methodParams);
    }

    static Set<ConstraintViolation<Object>> checkConstructorParams(Object[] paramValues) throws Exception {

        StackFrame stackFrame = Reflections.getCurrentStackFrame(3);
        String thisClassName = stackFrame.getClassName();
        Class<?> thisObjClass = Class.forName(thisClassName);

        return checkConstructorParamsInternal(thisObjClass, paramValues);
    }

    private static Set<ConstraintViolation<Object>> checkConstructorParamsInternal(
        Class<?> thisObjClass,
        Object[] constructorParams) {

        Constructor<Object> constructor = findMatchingConstructor(thisObjClass, constructorParams);
        return Validations.EXECUTABLE_VALIDATOR.validateConstructorParameters(constructor, constructorParams);
    }

    private static Method findMatchingMethod(Class<?> methodClass, String methodName, Object[] paramValues) {
        return findSingularMatchingMethod(methodClass, methodName)
            .or(() -> findSimilarMatchingMethod(methodClass, methodName, paramValues))
            .or(() -> findMethodByParamCount(methodClass, methodName, paramValues.length))
            .orElseThrow(() -> new IllegalArgumentException("No matching method found."));
    }

    private static Constructor<Object> findMatchingConstructor(Class<?> constructorClass, Object[] paramValues) {
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
    private static Optional<Method> findSimilarMatchingMethod(Class<?> methodClass, String methodName, Object[] paramValues) {
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
    @SuppressWarnings("unchecked")
    private static Optional<Constructor<Object>> findSimilarMatchingConstructor(
        Class<?> constructorClass,
        Object[] paramValues) {

        return Arrays.stream(constructorClass.getDeclaredConstructors())
            .filter(constructor -> isSimilarConstructor(constructor, paramValues))
            .findFirst()
            .map(constructor -> (Constructor<Object>) constructor);
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
    static boolean isSimilarConstructor(Constructor<?> constructor, Object[] paramValues) {

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

    @SuppressWarnings("unchecked")
    private static Optional<Constructor<Object>> findConstructorByParamCount(Class<?> constructorClass, int paramCount) {

        List<Constructor<?>> matchingConstructors = Arrays
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

        return Optional.of((Constructor<Object>) matchingConstructors.get(0));
    }
}
