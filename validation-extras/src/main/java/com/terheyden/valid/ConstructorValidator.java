package com.terheyden.valid;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Used to validate an individual constructor's parameters.
 * This gets created with: {@link Validations#createConstructorValidator(Class, String)} et. al.
 *
 * @param classType  the class with the constructor to validate
 * @param constructorToValidate the version of the constructor to validate
 */
public record ConstructorValidator(Class<?> classType, Constructor<?> constructorToValidate) {

    /**
     * Performs Jakarta Bean Validation on the constructor's parameters, returning any errors.
     * Due to the way Jakarta Bean Validation works, ALL constructor params must be passed in.
     *
     * @param allConstructorParams all the parameters passed into the constructor
     * @return a set of constraint violations, or an empty set if no violations were found.
     *     Use {@link Validations#parseViolationMessages(Collection)} if you prefer easy-to-read string results
     */
    public Set<? extends ConstraintViolation<?>> checkParams(Object... allConstructorParams) {
        return Validations.checkParams(constructorToValidate, allConstructorParams);
    }

    /**
     * Perform Jakarta Bean Validation on a constructor's parameters, throwing an exception if any violations are found.
     * Due to how Jakarta Bean Validation works, ALL constructor parameters must be passed in.
     *
     * @param allConstructorParams all the parameters coming into the constructor
     * @throws ConstraintViolationException if any violations are found
     */
    public void validateParams(Object... allConstructorParams) {
        Validations.validateParams(constructorToValidate, allConstructorParams);
    }
}
