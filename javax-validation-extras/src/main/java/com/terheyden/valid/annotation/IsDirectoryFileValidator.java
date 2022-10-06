package com.terheyden.valid.annotation;

import javax.annotation.Nullable;
import java.io.File;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates that a {@link File} exists and is a directory.
 */
public class IsDirectoryFileValidator implements ConstraintValidator<IsDirectory, File> {

    @Override
    public boolean isValid(
        @Nullable File file,
        ConstraintValidatorContext context) {

        // As is custom with Jakarta Bean Validation, nulls are checked in their own annotation / validator.
        if (file == null) {
            return true;
        }

        if (!file.exists()) {
            return Validations.invalid(context, "Directory does not exist: " + file);
        }

        if (!file.isDirectory()) {
            return Validations.invalid(context, "Not a directory: " + file);
        }

        return true;
    }
}
