package com.terheyden.valid.examples;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Example class-level annotation.
 * The only difference is the @Target.
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE }) // TYPE = a class, interface, or enum
@Constraint(validatedBy = ValidEmployeeValidator.class)
public @interface ValidEmployee {

    String message() default "Employee is invalid.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
