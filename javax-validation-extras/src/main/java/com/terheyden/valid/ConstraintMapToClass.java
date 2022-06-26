package com.terheyden.valid;

import java.lang.annotation.Annotation;

import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;

import javax.validation.ConstraintValidator;

/**
 * Describes a relationship between a constraint annotation (IsNull.class)
 * and a validator (IsNullValidator.class).
 */
/* package */ class ConstraintMapToClass<A extends Annotation> implements ConstraintMapper<A> {

    // I know this isn't used but it makes the generic typing easier.
    private final Class<A> annotationClass;
    private final Class<? extends ConstraintValidator<A, ?>> validatorClass;

    /* package */ ConstraintMapToClass(
        Class<A> annotationClass,
        Class<? extends ConstraintValidator<A, ?>> validatorClass) {

        this.annotationClass = annotationClass;
        this.validatorClass = validatorClass;
    }

    @Override
    public ConstraintDefinitionContext<A> addConstraint(ConstraintDefinitionContext<A> context) {
        return context.validatedBy(validatorClass);
    }
}
