package com.terheyden.valid.examples;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Example that programmatically associates a validator with an annotation constraint.
 * Annotations are well placed in a 'libs' dir or module, but annotation processing
 * could be better off in a logic module.
 *
 * So notice here that there is no @Constraint validator defined below.
 */
@Documented
@Retention(RUNTIME)
@Constraint(validatedBy = { })
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
public @interface ProgramConstraint {

    String message() default "Constraint failed for some reason.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
