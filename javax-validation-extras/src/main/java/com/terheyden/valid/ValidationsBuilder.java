package com.terheyden.valid;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
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
     * When utilizing the fluent builder, we need to group mappings by annotation type,
     * so this map is used to store the mappings.
     *
     * An annotation may either be mapped to a validation class (@link ConstraintMapToClass)
     * or a validation function (@link ConstraintMapToFunction). Both of those types extend
     * {@link ConstraintMapper}.
     */
    private final Map<Class, List<ConstraintMapper>> constraints = new HashMap<>();

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

        // A validation annotation may have multiple types associated with it
        // (for example, @NotEmpty works for Strings and Lists and each type needs its own validator).
        // We store those mappings and build them all at the end.
        constraints.computeIfAbsent(
            annotationClass,
            annClass -> new ArrayList<>()
            ).add(new ConstraintMapToClass<>(annotationClass, validatorClass));

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

        // A validation annotation may have multiple types associated with it
        // (for example, @NotEmpty works for Strings and Lists and each type needs its own validator).
        // We store those mappings and build them all at the end.
        constraints.computeIfAbsent(
            annotationClass,
            annClass -> new ArrayList<>()
            ).add(new ConstraintMapToFunction<>(annotationClass, typeToValidate, validationFunction));

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

        // A validation annotation may only be defined ONCE in [constraintMapping].
        // Here's an example of how to add two validators:
        //     constraintMapping
        //         .constraintDefinition(IsNull.class)
        //             .validatedBy(IsNullValidator.class) // mapping 1
        //             .validateType(String.class)         // mapping 2
        //                 .with(str -> str == null)

        for (Class annotationClass : constraints.keySet()) {

            ConstraintDefinitionContext constraintCtx = constraintMapping.constraintDefinition(annotationClass);

            for (ConstraintMapper constraint : constraints.get(annotationClass)) {
                constraint.addConstraint(constraintCtx);
            }
        }

        return configuration.addMapping(constraintMapping).buildValidatorFactory();
    }
}
