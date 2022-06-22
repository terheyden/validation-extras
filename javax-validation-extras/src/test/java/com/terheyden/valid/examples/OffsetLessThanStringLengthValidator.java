package com.terheyden.valid.examples;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

/**
 * Example method-level (cross-parameter) constraint validator.
 *
 * First, the validator must be annotated with: @SupportedValidationTarget(ValidationTarget.PARAMETER)
 * Second, the target type must be: Object[]
 */
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class OffsetLessThanStringLengthValidator implements ConstraintValidator<OffsetLessThanStringLength, Object[]> {

    /**
     * This does the actual validation.
     *
     * @param params array of parameters to validate
     * @param context context in which the constraint is evaluated
     */
    @Override
    public boolean isValid(Object[] params, ConstraintValidatorContext context) {

        String fullStr = Validations.getObjectParam(params[0]);
        int offset = Validations.getObjectParam(params[1]);

        // The method returns a substring, so we want to validate that
        // the offset is less than the length of the string.
        if (offset < 0 || offset >= fullStr.length()) {
            return Validations.invalid(context,
                "Offset (%d) must be >= 0 and < the length of the string: %s (%d)",
                offset,
                fullStr,
                fullStr.length());
        }

        return true;
    }
}

