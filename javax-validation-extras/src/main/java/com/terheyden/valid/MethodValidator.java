package com.terheyden.valid;

import java.lang.reflect.Method;

import javax.validation.ConstraintViolationException;

/**
 * Used to validate an individual method's parameters.
 * This gets created with: {@link Validations#createMethodValidator(Class, String)} et. al.
 *
 * @param classType  the class that contains the method to validate
 * @param methodToValidate the method to validate
 */
public record MethodValidator(Class<?> classType, Method methodToValidate) {

    /**
     * Perform Jakarta Bean Validation on a method's parameters, throwing an exception if any violations are found.
     * Due to how Jakarta Bean Validation works, you must pass ALL method parameters
     * to this call, even if you don't need to validate all of them.
     *
     * @param thisClassObject the instance that contains the method to validate, i.e., 'this'
     * @param allMethodParams all the parameters coming into the method
     * @throws ConstraintViolationException if any violations are found
     */
    public void validate(Object thisClassObject, Object... allMethodParams) {
        Validations.validateParams(thisClassObject, methodToValidate, allMethodParams);
    }
}
