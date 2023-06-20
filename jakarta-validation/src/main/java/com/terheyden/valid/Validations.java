package com.terheyden.valid;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;

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

    private Validations() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Perform Jakarta Bean Validation on the given object, returning any violations.
     * To throw an exception if any violations are found, use {@link #validateObject(Object)} instead.
     *
     * @param objectToValidate the object to validate; null is considered invalid
     * @return a set of constraint violations, or an empty set if no violations were found
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<ConstraintViolation<T>> checkObject(@Nullable T objectToValidate) {

        return objectToValidate == null
            ? Collections.singleton(NULL_ORIGIN_VIOLATION)
            : VALIDATOR.validate(objectToValidate);
    }

    public static <T> Set<ConstraintViolation<T>> checkMethodArgs(T thisObj, Object... methodArgs) {
        try {

            return ArgValidation.checkMethodArgs(thisObj, methodArgs);

        } catch (Exception e) {
            return ValidUtils.throwUnchecked(e);
        }
    }

    public static Set<ConstraintViolation<Object>> checkConstructorArgs(Object... constructorArgs) {
        try {

            return ArgValidation.checkConstructorArgs(constructorArgs);

        } catch (Exception e) {
            return ValidUtils.throwUnchecked(e);
        }
    }

    /**
     * Perform Jakarta Bean Validation on the given object, throwing an exception if any violations are found.
     * To get a list of violations (without throwing) instead, use {@link #checkObject(Object)} instead.
     *
     * @param objectToValidate the object to validate; null is considered invalid
     * @throws NullPointerException if the object to validate is null
     * @throws ConstraintViolationException if any violations are found
     * @return {@code objectToValidate}, for chaining
     */
    public static <T> T validateObject(@Nullable T objectToValidate) {

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

    public static void validateMethodArgs(Object thisObj, Object... methodArgs) {

        Set<ConstraintViolation<Object>> violations = null;

        try {
            violations = ArgValidation.checkMethodArgs(thisObj, methodArgs);
        } catch (Exception e) {
            ValidUtils.throwUnchecked(e);
        }

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public static void validateConstructorArgs(Object... methodArgs) {

        Set<ConstraintViolation<Object>> violations = null;

        try {
            violations = ArgValidation.checkConstructorArgs(methodArgs);
        } catch (Exception e) {
            ValidUtils.throwUnchecked(e);
        }

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
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
