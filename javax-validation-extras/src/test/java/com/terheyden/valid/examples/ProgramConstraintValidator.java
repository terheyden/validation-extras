package com.terheyden.valid.examples;

import javax.annotation.Nullable;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * To dynamically link this validator to {@link ProgramConstraint}, all that's needed is:
 *     1. Create the file: META-INF/services/jakarta.validation.ConstraintValidator
 *     2. Add the line: "com.terheyden.valid.examples.ProgramConstraintValidator"
 * The assocation to the right annotation happens automatically.
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
