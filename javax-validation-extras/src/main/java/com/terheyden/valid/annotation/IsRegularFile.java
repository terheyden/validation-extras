package com.terheyden.valid.annotation;

import java.io.File;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Path;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Jakarta Bean Validation to verify that a {@link Path} or {@link File} exists and is a regular file.
 */
@Documented
@Retention(RUNTIME)
@Constraint(validatedBy = {
    IsFileValidator.class,
    IsFilePathValidator.class
})
@Target({ FIELD, PARAMETER, METHOD, TYPE_PARAMETER })
public @interface IsRegularFile {

    /**
     * Returns the default error message.
     * Most of the time you'll just override this in the {@link ConstraintValidator}.
     */
    String message() default "File does not exist or is not a regular file";

    /**
     * The groups (categories) to which this constraint belongs, if any.
     * Usually just left blank.
     */
    Class<?>[] groups() default { };

    /**
     * Payload allows you to associate arbitrary data with the constraint.
     * E.g. {@code @NotNull(payload = { Severity.ERROR.class })}
     * Usually just left blank.
     */
    Class<? extends Payload>[] payload() default { };
}
