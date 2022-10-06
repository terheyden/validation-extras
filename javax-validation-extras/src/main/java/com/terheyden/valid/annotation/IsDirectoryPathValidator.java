package com.terheyden.valid.annotation;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates that a {@link Path} exists and is a directory.
 */
public class IsDirectoryPathValidator implements ConstraintValidator<IsDirectory, Path> {

    @Override
    public boolean isValid(
        @Nullable Path path,
        ConstraintValidatorContext context) {

        // As is custom with Jakarta Bean Validation, nulls are checked in their own annotation / validator.
        if (path == null) {
            return true;
        }

        if (Files.notExists(path)) {
            return Validations.invalid(context, "Directory does not exist: " + path);
        }

        if (!Files.isDirectory(path)) {
            return Validations.invalid(context, "Not a directory: " + path);
        }

        return true;
    }
}
