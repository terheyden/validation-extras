package com.terheyden.valid.examples;

import javax.annotation.Nullable;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 */
public class ProgramConstraintValidator implements ConstraintValidator<MyConstraint, Object> {

    /**
     * We'll just check for null in this example.
     */
    @Override
    public boolean isValid(@Nullable Object objToValidate, ConstraintValidatorContext context) {

        if (objToValidate == null) {
            return Validations.invalid(context, "Must not be null.");
        }

        return true;
    }
}
