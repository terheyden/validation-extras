package com.terheyden.valid;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.validator.internal.engine.path.PathImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.ConstraintDescriptor;

import static java.lang.String.format;

/**
 * Jakarta Bean Validation-related utilities.
 * Dependency info is at the bottom of this file.
 */
public final class Validations {

    /**
     * The default validator factory.
     * Immutable and thread-safe.
     */
    public static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

    /**
     * The default validator.
     * Immutable and thread-safe.
     */
    public static final Validator VALIDATOR = FACTORY.getValidator();

    /**
     * The default executable validator.
     * Immutable and thread-safe.
     */
    public static final ExecutableValidator EXECUTABLE_VALIDATOR = VALIDATOR.forExecutables();

    // Thrown when we try to validate a null object.
    private static final NullOriginViolation NULL_ORIGIN_VIOLATION = new NullOriginViolation();
    private static final String EMPTY_STR = "";

    private Validations() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Perform Jakarta Bean Validation on the given object, returning any violations.
     * To throw an exception if any violations are found, use {@link #validate(Object)} instead.
     *
     * @param objectToValidate the object to validate; null is considered invalid
     * @return a set of constraint violations, or an empty set if no violations were found
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<ConstraintViolation<T>> check(@Nullable T objectToValidate) {

        return objectToValidate == null
            ? Collections.singleton(NULL_ORIGIN_VIOLATION)
            : VALIDATOR.validate(objectToValidate);
    }

    /**
     * Perform Jakarta Bean Validation on the given object, returning any violations as Human-readable strings.
     * To throw an exception if any violations are found, use {@link #validate(Object)} instead.
     *
     * @param objectToValidate the object to validate; null is considered invalid
     * @return a list of constraint violation descriptions, or an empty list if no violations were found
     */
    public static List<String> checkToList(@Nullable Object objectToValidate) {
        return check(objectToValidate)
            .stream()
            .map(Validations::violationToString)
            .collect(Collectors.toList());
    }

    /**
     * Perform Jakarta Bean Validation on the given object, returning any violations as Human-readable strings.
     * To throw an exception if any violations are found, use {@link #validate(Object)} instead.
     *
     * @param objectToValidate the object to validate; null is considered invalid
     * @return a description of any violations found, or an empty string if none were found
     */
    public static String checkToString(@Nullable Object objectToValidate) {
        return violationsToString(check(objectToValidate));
    }

    /**
     * Perform Jakarta Bean Validation on the given object, throwing an exception if any violations are found.
     * To get a list of violations (without throwing) instead, use {@link #checkToList(Object)}.
     *
     * @param objectToValidate the object to validate; null is considered invalid
     * @throws NullPointerException if the object to validate is null
     * @throws ConstraintViolationException if any violations are found
     * @return {@code objectToValidate}, for chaining
     */
    public static <T> T validate(@Nullable T objectToValidate) {

        if (objectToValidate == null) {
            throw new NullPointerException("Object to validate is null.");
        }

        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(objectToValidate);

        if (violations.isEmpty()) {
            // Looks good, return.
            return objectToValidate;
        }

        throw new ConstraintViolationException(violations);
    }

    /**
     * Create a human-readable error message from a constraint violation.
     */
    public static String violationToString(@Nullable ConstraintViolation<?> violation) {

        if (violation == null) {
            return EMPTY_STR;
        }

        String className = violation.getRootBeanClass().getSimpleName();
        String propertyPath = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        // The property path will be an empty string, if the violation is on the class itself.
        return propertyPath.isEmpty()
            ? format("%s: %s", className, message)
            : format("%s.%s: %s", className, propertyPath, message);
    }

    /**
     * Return a single human-readable error string for the given constraint violations.
     */
    public static String violationsToString(@Nullable Collection<? extends ConstraintViolation<?>> violations) {

        if (violations == null || violations.isEmpty()) {
            return EMPTY_STR;
        }

        return violations.stream()
            .map(Validations::violationToString)
            .collect(Collectors.joining("; "));
    }

    /**
     * If the object to validate is null, return a special "null violation" object.
     */
    @SuppressWarnings("rawtypes")
    public static final class NullOriginViolation implements ConstraintViolation {

        @Override
        public String getMessage() {
            return "Object to validate is null.";
        }

        @Override
        public String getMessageTemplate() {
            return getMessage();
        }

        @Override
        public Object getRootBean() {
            return this;
        }

        @Override
        public Class getRootBeanClass() {
            return NullOriginViolation.class;
        }

        @Override
        public Object getLeafBean() {
            return this;
        }

        @Override
        @Nullable
        public Object[] getExecutableParameters() {
            return null;
        }

        @Override
        @Nullable
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return PathImpl.createPathFromString(EMPTY_STR);
        }

        @Override
        @Nullable
        public Object getInvalidValue() {
            return null;
        }

        @Override
        @Nullable
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public Object unwrap(Class type) {
            return this;
        }
    }
}
/*

DEPENDENCIES
============

JAKARTA / SPRING BOOT 3 VERSION:

Gradle / 8:

// https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator
implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
// https://mvnrepository.com/artifact/org.glassfish/jakarta.el
implementation("org.glassfish:jakarta.el:4.0.2")

Maven / 7:

<!-- These next two dependencies are for Jakarta Bean Validation support.
     The Hibernate team provides the reference implementation -->
<!-- This transitively pulls in the dependency to the Jakarta Bean Validation API
     (jakarta.validation:jakarta.validation-api:3.0.0)
     https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>7.0.5.Final</version>
</dependency>
<!-- Hibernate Validator requires an implementation of Jakarta Expression Language
     for evaluating dynamic expressions in constraint violation messages,
     even if we're not actually using it (I checked) -->
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>jakarta.el</artifactId>
    <version>4.0.2</version>
</dependency>

JAVAX / SPRING BOOT 2 VERSION:

Gradle:

// https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator
implementation("org.hibernate.validator:hibernate-validator:6.2.5.Final")
// https://mvnrepository.com/artifact/org.glassfish/jakarta.el
testImplementation("org.glassfish:jakarta.el:3.0.4")

Maven:

<!-- These next two dependencies are for Jakarta Bean Validation support.
     The Hibernate team provides the reference implementation -->
<!-- https://hibernate.org/validator/documentation/ -->
<!-- This transitively pulls in the dependency to the Jakarta Bean Validation API
     (jakarta.validation:jakarta.validation-api:2.0.2)
     https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.2.5.Final</version>
</dependency>
<!-- Hibernate Validator requires an implementation of Jakarta Expression Language
     for evaluating dynamic expressions in constraint violation messages,
     even if we're not actually using it (I checked) -->
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>jakarta.el</artifactId>
    <version>3.0.4</version>
</dependency>

 */
