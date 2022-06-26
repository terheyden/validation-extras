package com.terheyden.valid;

import java.lang.annotation.Annotation;

import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext.ValidationCallable;

/**
 * Describes a way of mapping a validation constraint to a validation function.
 */
/* package */ class ConstraintMapToFunction<A extends Annotation, T> implements ConstraintMapper<A> {

    // I know this isn't used but it makes the generic typing easier.
    private final Class<A> annotationClass;
    private final Class<T> typeToValidate;
    private final ValidationCallable<T> validationFunction;

    /* package */ ConstraintMapToFunction(
        Class<A> annotationClass,
        Class<T> typeToValidate,
        ValidationCallable<T> validationFunction) {

        this.annotationClass = annotationClass;
        this.typeToValidate = typeToValidate;
        this.validationFunction = validationFunction;
    }

    @Override
    public ConstraintDefinitionContext<A> addConstraint(ConstraintDefinitionContext<A> context) {
        return context
            .validateType(typeToValidate)
            .with(validationFunction);
    }
}
