package com.terheyden.valid.examples;

import javax.annotation.Nullable;
import java.util.Optional;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Example validator that affirms that an {@link Optional} has a value (is not empty).
 */
public class IsPresentValidator implements ConstraintValidator<IsPresent, Optional<?>> {

    /**
     * This does the actual validation.
     *
     * @param optionalToValidate Optional to validate
     * @param context            context in which the constraint is evaluated
     */
    @Override
    public boolean isValid(@Nullable Optional<?> optionalToValidate, ConstraintValidatorContext context) {

        if (optionalToValidate != null && optionalToValidate.isPresent()) {
            return true;
        }

        return Validations.invalid(context, "Optional value must be present.");
    }
}

