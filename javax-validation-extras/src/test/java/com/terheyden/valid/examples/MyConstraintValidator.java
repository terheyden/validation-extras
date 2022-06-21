package com.terheyden.valid.examples;

import javax.annotation.Nullable;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Used by Jakarta Bean Validation to validate Strings annotated with \@{@link MyConstraint}.
 * TODO: Verifies...
 */
public class MyConstraintValidator implements ConstraintValidator<MyConstraint, String> {

    private String mySetting = "";

    /**
     * This lets you grab the settings from the annotation and store them before validating.
     */
    @Override
    public void initialize(MyConstraint annotation) {
        this.mySetting = annotation.value();
    }

    /**
     * This does the actual validation.
     *
     * @param stringToValidate object to validate
     * @param context context in which the constraint is evaluated
     */
    @Override
    public boolean isValid(@Nullable String stringToValidate, ConstraintValidatorContext context) {

        if (stringToValidate == null) {
            return Validations.invalid(context, "Must not be null.");
        }

        // Just an example — String must be all lowercase:
        if (!stringToValidate.equals(stringToValidate.toLowerCase())) {
            return Validations.invalid(context, "Must be lowercase.");
        }

        return true;
    }
}
