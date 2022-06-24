package com.terheyden.valid;

import java.lang.annotation.Annotation;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext.ValidationCallable;

import javax.validation.ConstraintValidator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * Used to create dynamic relationships between validators and constraints.
 * Example usage:
 * <pre>
 * {@code
 * ValidationsBuilder builder = new ValidationsBuilder();
 * builder.constraintMapping()
 *     .type(User.class)
 *     ...etc...
 * Validations.setValidator(builder);
 * }
 * </pre>
 * <p>
 * Note that, currently, you cannot add more than one constraint to an annotation.
 * For that, you'll have to do it long-form via {@link #constraintMapping()}.
 *
 * @see
 * <a href="https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-programmatic-api">
 *     Programmatic API Reference
 * </a>
 */
public class ValidationsBuilder {

    /**
     * In order to create a custom, dynamic, programmatic validator,
     * we switch over to the {@link HibernateValidator}.
     */
    private final HibernateValidatorConfiguration configuration = Validation
        .byProvider(HibernateValidator.class)
        .configure();

    /**
     * This one mapping object can have many constraints added to it.
     */
    private final ConstraintMapping constraintMapping = configuration.createConstraintMapping();

    /**
     * Programmatically add a validator to a constraint annotation.
     *
     * @param annotationClass the constraint annotation to associate with the validator
     * @param validatorClass the validator to associate with the constraint annotation
     * @see
     * <a href="https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-constraint-definition-contribution">
     *     Hibernate Reference
     * </a>
     */
    public <A extends Annotation> ValidationsBuilder addConstraintValidator(
        Class<A> annotationClass,
        Class<? extends ConstraintValidator<A, ?>> validatorClass) {

        constraintMapping
            .constraintDefinition(annotationClass)
            .validatedBy(validatorClass);

        return this;
    }

    /**
     * Add a validator to a constraint annotation as a lambda function.
     *
     * @param annotationClass the constraint annotation to associate with the validator
     * @param typeToValidate type of object to validate
     * @param validationFunction the validation function to use — true if valid, false if invalid
     * @see
     * <a href="https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-constraint-definition-contribution">
     *     Hibernate Reference
     * </a>
     */
    public <A extends Annotation, T> ValidationsBuilder addConstraintValidator(
        Class<A> annotationClass,
        Class<T> typeToValidate,
        ValidationCallable<T> validationFunction) {

        constraintMapping.constraintDefinition(annotationClass)
            .validateType(typeToValidate)
            .with(validationFunction);

        return this;
    }

    /**
     * Use this only if you need full control over the Hibernate Validator configuration.
     * For example:
     * <pre>
     * {@code
     * builder.constraintMapping()
     *    .type(User.class)
     *        .field("name")
     *        .constraint(new GenericConstraintDef<>(MyConstraint.class))
     *        .param("value", "ConfigValue")
     *        .valid()
     *    .type(Employee.class)
     *        .field("id")
     *        ...etc.
     * }
     * </pre>
     *
     * @see
     * <a href="https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-programmatic-api">
     *     Programmatic API Reference
     * </a>
     */
    public ConstraintMapping constraintMapping() {
        return constraintMapping;
    }

    /**
     * Build the custom validation factory.
     */
    /* package */ ValidatorFactory build() {
        return configuration.addMapping(constraintMapping).buildValidatorFactory();
    }
}
