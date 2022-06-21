package com.terheyden.valid.examples;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.terheyden.valid.examples.MyConstraint.List;

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
 * TODO: The annotated element...
 */
@Documented
@Retention(RUNTIME)
// TODO: If this annotation cannot be repeated, delete @Repeatable and the nested List interface below.
@Repeatable(List.class)
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
// TODO: If combining annotations you can set validatedBy = { }.
@Constraint(validatedBy = MyConstraintValidator.class)
public @interface MyConstraint {

    /**
     * Returns the default error message.
     * This is usually overridden by the {@link ConstraintValidator}.
     */
    String message() default "Must be...";

    /**
     * The groups (categories) to which this constraint belongs. Usually left blank.
     */
    Class<?>[] groups() default { };

    /**
     * Payload allows you to associate arbitrary data with the constraint. Usually left blank.
     */
    Class<? extends Payload>[] payload() default { };

    /**
     * TODO: 'value' is an annotation's default value, if needed.
     */
    String value() default "";

    /**
     * Gives support for repeating this annotation, usually with different settings.
     * The Jakarta Bean Validation spec suggests using the @interface name 'List'.
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
    @interface List {
        MyConstraint[] value();
    }
}
