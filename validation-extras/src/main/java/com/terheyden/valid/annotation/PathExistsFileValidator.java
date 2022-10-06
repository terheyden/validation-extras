package com.terheyden.valid.annotation;

import javax.annotation.Nullable;
import java.io.File;

import com.terheyden.valid.Validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that a {@link File} exists.
 */
public class PathExistsFileValidator implements ConstraintValidator<PathExists, File> {

    @Override
    public boolean isValid(
        @Nullable File file,
        ConstraintValidatorContext context) {

        // As is custom with Jakarta Bean Validation, nulls are checked in their own annotation / validator.
        if (file == null) {
            return true;
        }

        if (file.exists()) {
            return true;
        }

        return Validations.invalid(context, "Path does not exist: %s", file);
    }
}
