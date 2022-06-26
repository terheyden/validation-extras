package com.terheyden.valid;

import java.lang.annotation.Annotation;

import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;

/**
 * Describes a way of mapping a validation constraint to a validator,
 * either a validation class or a validation function.
 */
/* package */ interface ConstraintMapper<A extends Annotation> {

        ConstraintDefinitionContext<A> addConstraint(ConstraintDefinitionContext<A> context);
}
