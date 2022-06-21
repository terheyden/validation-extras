package com.terheyden.valid;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Used to validate an individual method's parameters.
 * This gets created with: {@link Validations#createMethodValidator(Class, String)} et. al.
 *
 * @param classType  the class that contains the method to validate
 * @param methodToValidate the method to validate
 */
public record MethodValidator(Class<?> classType, Method methodToValidate) {

    /**
     * Performs Jakarta Bean Validation on the method's parameters, returning any errors.
     * Due to the way Jakarta Bean Validation works, ALL method params must be passed in.
     *
     * @param thisClassObject the object instance containing the method to validate, i.e. 'this'
     * @param allMethodParams all the parameters passed into the method
     * @return a set of constraint violations, or an empty set if no violations were found.
     *     Use {@link Validations#parseViolationMessages(Collection)} if you prefer easy-to-read string results
     */
    public Set<? extends ConstraintViolation<?>> checkParams(Object thisClassObject, Object... allMethodParams) {
        return Validations.checkParams(thisClassObject, methodToValidate, allMethodParams);
    }

    /**
     * Perform Jakarta Bean Validation on a method's parameters, throwing an exception if any violations are found.
     * Due to how Jakarta Bean Validation works, you must pass ALL method parameters
     * to this call, even if you don't need to validate all of them.
     *
     * @param thisClassObject the instance that contains the method to validate, i.e., 'this'
     * @param allMethodParams all the parameters passed into the method
     * @throws ConstraintViolationException if any violations are found
     */
    public void validateParams(Object thisClassObject, Object... allMethodParams) {
        Validations.validateParams(thisClassObject, methodToValidate, allMethodParams);
    }
}
