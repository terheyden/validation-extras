package com.terheyden.valid.annotation;

import javax.annotation.Nullable;
import java.io.File;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates that a {@link File} exists and is a regular file.
 */
public class IsFileValidator implements ConstraintValidator<IsRegularFile, File> {

    @Override
    public boolean isValid(
        @Nullable File file,
        ConstraintValidatorContext context) {

        // As is custom with Jakarta Bean Validation, nulls are checked in their own annotation / validator.
        if (file == null) {
            return true;
        }

        if (!file.exists()) {
            return Validations.invalid(context, "File does not exist: " + file);
        }

        if (!file.isFile()) {
            return Validations.invalid(context, "Not a regular file: " + file);
        }

        return true;
    }
}
