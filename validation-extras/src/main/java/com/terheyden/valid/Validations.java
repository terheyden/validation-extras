package com.terheyden.valid;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;

import static com.terheyden.valid.ValidationUtils.createViolationString;

/**
 * Jakarta Bean Validation-related utilities.
 * Example usage — validate a User object, and print out any violations:
 *     Validations.validate(user).forEach(System.out::println);
 */
public final class Validations {

    /**
     * The default validator.
     * Thread-safe, immutable, and reusable.
     */
    /* package */ static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    /* package */ static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();
    /* package */ static final ExecutableValidator METHOD_VALIDATOR = VALIDATOR.forExecutables();

    private Validations() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Perform Jakarta Bean Validation on the given object, returning any error strings.
     *
     * @return a list of constraint violations, or an empty list if no violations were found
     */
    public static List<String> check(Object objectToValidate) {
        return VALIDATOR.validate(objectToValidate)
            .stream()
            .map(ValidationUtils::createViolationString)
            .toList();
    }

    /**
     * Perform Jakarta Bean Validation on the given object, throwing an exception if any violations are found.
     *
     * @param objectToValidate the object to validate; may not be null
     * @throws ValidationException if the object to validate is null
     * @throws ConstraintViolationException if any violations are found
     */
    public static void validate(@Nullable Object objectToValidate) {

        if (objectToValidate == null) {
            throw new ValidationException("Object to validate cannot be null.");
        }

        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(objectToValidate);

        if (violations.isEmpty()) {
            return;
        }

        throw new ConstraintViolationException(createViolationString(violations), violations);
    }

    /**
     * Nice little helper method for setting a custom constraint violation message and returning {@code false}
     * if the constraint is violated. Call this inside your {@code MyConstraintValidator.isValid()} method.
     */
    public static boolean violation(ConstraintValidatorContext context, String errorMessage, Object... args) {

        context.disableDefaultConstraintViolation();

        String formattedError = args.length == 0 ? errorMessage : errorMessage.formatted(args);

        context
            .buildConstraintViolationWithTemplate(formattedError)
            .addConstraintViolation();

        return false;
    }

    /**
     * Creates a reusable parameter validator for the given method. Locates the method by name,
     * so there must be no method overloads (e.g. {@code myMethod(String)} and {@code myMethod(int)}).
     * If your method does have overloads, try using {@link #createMethodValidator(Class, String, int)}
     * or {@link #createMethodValidator(Class, String, Class)} instead.
     *
     * @param classType the class that contains the method to validate
     * @param methodName the name of the method to validate
     * @return a reusable parameter validator for the given method
     */
    public static MethodValidator createMethodValidator(Class<?> classType, String methodName) {
        return new MethodValidator(classType, MethodValidations.findMethod(classType, methodName));
    }

    /**
     * Creates a reusable parameter validator for the given method. Locates the method by name and
     * unique parameter type. For example, if you have {@code myMethod(String, String)}, and
     * @code myMethod(String, Integer)}, calling {@code createMethodValidator(MyClass.class, "myMethod", Integer.class)}
     * would use the second method (because it is the version with an {@code Integer} parameter.
     * <p>
     * See also {@link #createMethodValidator(Class, String)} and {@link #createMethodValidator(Class, String, int)}.
     *
     * @param classType the class that contains the method to validate
     * @param methodName the name of the method to validate
     * @param anyParamType the type of a unique parameter to the method
     * @return a reusable parameter validator for the given method
     */
    public static MethodValidator createMethodValidator(Class<?> classType, String methodName, Class<?> anyParamType) {
        return new MethodValidator(classType, MethodValidations.findMethod(classType, methodName, anyParamType));
    }

    /**
     * Creates a reusable parameter validator for the given method. Locates the method by name and
     * parameter count. For example, if you have {@code myMethod(String)}, and
     * @code myMethod(String, Integer)}, calling {@code createMethodValidator(MyClass.class, "myMethod", 2)}
     * would use the second method.
     * <p>
     * See also {@link #createMethodValidator(Class, String)} and {@link #createMethodValidator(Class, String, Class)}.
     *
     * @param classType the class that contains the method to validate
     * @param methodName the name of the method to validate
     * @param paramCount the number of parameters to the method
     * @return a reusable parameter validator for the given method
     */
    public static MethodValidator createMethodValidator(Class<?> classType, String methodName, int paramCount) {
        return new MethodValidator(classType, MethodValidations.findMethod(classType, methodName, paramCount));
    }

    /**
     * Creates a reusable parameter validator for the given constructor. This version of
     * {@code createConstructorValidator()} only works if there is exactly one constructor for the class.
     * If the class has multiple constructors, try using {@link #createConstructorValidator(Class, Class)}
     * or {@link #createConstructorValidator(Class, int)} instead.
     *
     * @param classType the class that contains the constructor to validate
     * @return a reusable parameter validator for the given constructor
     */
    public static ConstructorValidator createConstructorValidator(Class<?> classType) {
        return new ConstructorValidator(classType, ConstructorValidations.findConstructor(classType));
    }

    /**
     * Creates a reusable parameter validator for the given constructor. Locates the constructor by
     * unique parameter type. For example, if you have {@code MyClass(String, String)}, and
     * @code MyClass(String, Integer)}, calling {@code createConstructorValidator(MyClass.class, Integer.class)}
     * would use the second constructor (because it is the version with an {@code Integer} parameter.
     * <p>
     * See also {@link #createConstructorValidator(Class)} and {@link #createConstructorValidator(Class, int)}.
     *
     * @param classType the class that contains the constructor to validate
     * @param anyParamType the type of a unique parameter to the constructor
     * @return a reusable parameter validator for the given constructor
     */
    public static ConstructorValidator createConstructorValidator(Class<?> classType, Class<?> anyParamType) {
        return new ConstructorValidator(classType, ConstructorValidations.findConstructor(classType, anyParamType));
    }

    /**
     * Creates a reusable parameter validator for the given constructor. Locates the constructor by
     * number of parameters. For example, if you have {@code MyClass(String)}, and
     * @code MyClass(String, Integer)}, calling {@code createConstructorValidator(MyClass.class, 2)}
     * would use the second constructor.
     * <p>
     * See also {@link #createConstructorValidator(Class)} and {@link #createConstructorValidator(Class, Class)}.
     *
     * @param classType the class that contains the constructor to validate
     * @param paramCount the number of parameters to the constructor
     * @return a reusable parameter validator for the given constructor
     */
    public static ConstructorValidator createConstructorValidator(Class<?> classType, int paramCount) {
        return new ConstructorValidator(classType, ConstructorValidations.findConstructor(classType, paramCount));
    }

    /**
     * Performs Jakarta Bean Validation on the given method, returning any error strings.
     * Package-private — the user will invoke this via {@link MethodValidator#validate(Object, Object...)}.
     *
     * @param thisMethodObject the object instance containing the method to validate, i.e. 'this'
     * @param methodToValidate the method to validate
     * @param allMethodParams all the parameters coming into the method
     * @return a list of constraint violations, or an empty list if no violations were found
     */
    /* package */ static List<String> checkParams(
        Object thisMethodObject,
        Method methodToValidate,
        Object... allMethodParams) {

        return METHOD_VALIDATOR.validateParameters(thisMethodObject, methodToValidate, allMethodParams)
            .stream()
            .map(ValidationUtils::createViolationString)
            .toList();
    }

    /**
     * Perform Jakarta Bean Validation on a method's params, throwing an exception if any violations are found.
     * Package-private — the user will invoke this via {@link MethodValidator#validate(Object, Object...)}.
     *
     * @param thisMethodObject object instance where the method lives, i.e. 'this'
     * @param methodToValidate the method whose params we are validating
     * @param methodParams     the params to validate
     * @throws IllegalArgumentException if the object to validate is null
     * @throws ConstraintViolationException if any violations are found
     */
    /* package */ static void validateParams(
        Object thisMethodObject,
        Method methodToValidate,
        Object... methodParams) {

        Set<ConstraintViolation<Object>> violations = METHOD_VALIDATOR.validateParameters(
            thisMethodObject,
            methodToValidate,
            methodParams);

        if (violations.isEmpty()) {
            return;
        }

        throw new ConstraintViolationException(createViolationString(violations), violations);
    }

    /**
     * Performs Jakarta Bean Validation on the given constructor, returning any error strings.
     * Package-private — the user will invoke this via {@link ConstructorValidator#validate(Object...)}.
     *
     * @param constructorToValidate the constructor to validate
     * @param allConstructorParams all the parameters coming into the constructor
     * @return a list of constraint violations, or an empty list if no violations were found
     */
    /* package */ static List<String> checkParams(
        Constructor<?> constructorToValidate,
        Object... allConstructorParams) {

        return METHOD_VALIDATOR.validateConstructorParameters(constructorToValidate, allConstructorParams)
            .stream()
            .map(ValidationUtils::createViolationString)
            .toList();
    }

    /**
     * Perform Jakarta Bean Validation on a constructor's params, throwing an exception if any violations are found.
     * Package-private — the user will invoke this via {@link ConstructorValidator#validate(Object...)}.
     *
     * @param constructorToValidate the constructor whose params we are validating
     * @param constructorParams     the params to validate
     * @throws ConstraintViolationException if any violations are found
     */
    /* package */ static void validateParams(
        Constructor<?> constructorToValidate,
        Object... constructorParams) {

        Set<ConstraintViolation<Object>> violations = METHOD_VALIDATOR.validateConstructorParameters(
            constructorToValidate,
            constructorParams);

        if (violations.isEmpty()) {
            return;
        }

        throw new ConstraintViolationException(createViolationString(violations), violations);
    }
}
