package com.terheyden.valid.examples;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Example constraint that affirms that an {@link Optional} has a value (is not empty).
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Constraint(validatedBy = { })
public @interface IsPresent {

    /**
     * Returns the default error message.
     * This is usually overridden by the {@link ConstraintValidator}.
     */
    String message() default "Optional value must be present.";

    /**
     * The groups (categories) to which this constraint belongs. Usually left blank.
     */
    Class<?>[] groups() default { };

    /**
     * Payload allows you to associate arbitrary data with the constraint. Usually left blank.
     */
    Class<? extends Payload>[] payload() default { };
}

