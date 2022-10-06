package com.terheyden.valid.annotation;

import javax.annotation.Nullable;
import java.io.File;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates that a {@link File} exists.
 */
public class PathNotExistsFileValidator implements ConstraintValidator<PathNotExists, File> {

    @Override
    public boolean isValid(
        @Nullable File file,
        ConstraintValidatorContext context) {

        // As is custom with Jakarta Bean Validation, nulls are checked in their own annotation / validator.
        if (file == null) {
            return true;
        }

        if (!file.exists()) {
            return true;
        }

        return Validations.invalid(context, "Path already exists: %s", file);
    }
}
