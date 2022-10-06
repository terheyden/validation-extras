package com.terheyden.valid.annotation;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;

import com.terheyden.valid.Validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that a {@link Path} exists and is a regular file.
 */
public class IsFilePathValidator implements ConstraintValidator<IsRegularFile, Path> {

    @Override
    public boolean isValid(
        @Nullable Path path,
        ConstraintValidatorContext context) {

        // As is custom with Jakarta Bean Validation, nulls are checked in their own annotation / validator.
        if (path == null) {
            return true;
        }

        if (Files.notExists(path)) {
            return Validations.invalid(context, "File does not exist: " + path);
        }

        if (!Files.isRegularFile(path)) {
            return Validations.invalid(context, "Not a regular file: " + path);
        }

        return true;
    }
}
