package com.terheyden.valid.examples;

import javax.annotation.Nullable;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Example class-level annotation validator.
 * There's no difference between a field-level validator except the target type is our custom class.
 */
public class ValidEmployeeValidator implements ConstraintValidator<ValidEmployee, Employee> {

    /**
     * This does the actual validation.
     *
     * @param employeeToValidate Employee to validate
     * @param context context in which the constraint is evaluated
     */
    @Override
    public boolean isValid(@Nullable Employee employeeToValidate, ConstraintValidatorContext context) {

        if (employeeToValidate == null) {
            return Validations.invalid(context, "Must not be null.");
        }

        // Just an example, this doesn't actually make any sense:
        if (employeeToValidate.getAge() < 21) {
            return Validations.invalid(context, "Must be of drinking age!");
        }

        return true;
    }
}

