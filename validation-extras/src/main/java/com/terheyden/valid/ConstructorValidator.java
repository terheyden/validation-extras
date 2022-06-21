package com.terheyden.valid;

import java.lang.reflect.Constructor;

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
     * Perform Jakarta Bean Validation on a constructor's parameters, throwing an exception if any violations are found.
     * Due to how Jakarta Bean Validation works, you must pass ALL constructor parameters
     * to this call, even if you don't need to validate all of them.
     *
     * @param thisConstructorClassObject the instance that contains the constructor to validate, i.e., 'this'
     * @param allConstructorParams all the parameters coming into the constructor
     * @throws ConstraintViolationException if any violations are found
     */
    public void validate(Object... allConstructorParams) {
        Validations.validateParams(constructorToValidate, allConstructorParams);
    }
}
