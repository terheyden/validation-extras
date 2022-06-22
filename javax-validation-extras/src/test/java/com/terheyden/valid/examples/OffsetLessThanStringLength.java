package com.terheyden.valid.examples;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Example method-level (cross-parameter) constraint.
 * No difference between this and a regular constraint annotation!
 * Cross-param constraints also work on constructors.
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Constraint(validatedBy = OffsetLessThanStringLengthValidator.class)
public @interface OffsetLessThanStringLength {

    String message() default "Offset must be less than string length.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

