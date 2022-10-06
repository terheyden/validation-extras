package com.terheyden.valid.annotation;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;

import com.terheyden.valid.Validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates that a {@link Path} exists.
 */
public class PathExistsPathValidator implements ConstraintValidator<PathExists, Path> {

    @Override
    public boolean isValid(
        @Nullable Path path,
        ConstraintValidatorContext context) {

        // As is custom with Jakarta Bean Validation, nulls are checked in their own annotation / validator.
        if (path == null) {
            return true;
        }

        if (Files.exists(path)) {
            return true;
        }

        return Validations.invalid(context, "Path does not exist: %s", path);
    }
}
