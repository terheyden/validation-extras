package com.terheyden.valid;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import static com.terheyden.valid.ValidationUtils.createViolationString;

/**
 * Jakarta Bean Validation-related utilities.
 */
public final class Validations {

    /*
     * The default validators.
     * Thread-safe, immutable, and reusable.
     *
     * These are not final. The user can add their own programmatic validators.
     */

    /* package */ static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    /* package */ static Validator validator = validatorFactory.getValidator();
    /* package */ static ExecutableValidator methodValidator = validator.forExecutables();

    private Validations() {
        // Private since this class shouldn't be instantiated.
    }

    ////////////////////////////////////////
    // CUSTOMIZATION

    /**
     * Switch from using the default Jakarta Bean Validation validator to a custom Hibernate-specific validator.
     * This allows the user to dynamically associate validators with constraints.
     * <p>
     * This method isn't thread-safe. However, the validators don't have any state, and they
     * don't depend on each other, so I really don't think there will be an issue.
     * The worst thing that could happen is that one millisecond you're using the default validator,
     * and the next millisecond you're using the custom one.
     * <p>
     * There might be an issue also if you call setValidator() multiple times, on multiple threads,
     * at the exact same time.
     */
    public static void setValidator(ValidationsBuilder validationsBuilder) {
        validatorFactory = validationsBuilder.build();
        validator = validatorFactory.getValidator();
        methodValidator = validator.forExecutables();
    }

    ////////////////////////////////////////
    // VALIDATION — checking and validating

    /**
     * Perform Jakarta Bean Validation on the given object, returning any error strings.
     *
     * @return a set of constraint violations, or an empty set if no violations were found.
     *     Use {@link Validations#parseViolationMessages(Collection)} if you prefer easy-to-read string results
     */
    public static Set<? extends ConstraintViolation<?>> check(@Nullable Object objectToValidate) {
        return validator.validate(objectToValidate);
    }

    /**
     * Perform Jakarta Bean Validation on the given object, throwing an exception if any violations are found.
     *
     * @param objectToValidate the object to validate; may not be null
     * @throws IllegalArgumentException if the object to validate is null
     * @throws ConstraintViolationException if any violations are found
     * @return {@code objectToValidate}, for chaining
     */
    public static <T> T validate(@Nullable T objectToValidate) {

        Set<ConstraintViolation<Object>> violations = validator.validate(objectToValidate);

        if (violations.isEmpty()) {
            return objectToValidate;
        }

        throw new ConstraintViolationException(createViolationString(violations), violations);
    }

    /**
     * Performs Jakarta Bean Validation on the method's parameters, returning any errors.
     * Due to the way Jakarta Bean Validation works, ALL method params must be passed in.
     * Package-private — the user will invoke this via {@link MethodValidator#validateParams(Object, Object...)}.
     *
     * @param thisMethodObject the object instance containing the method to validate, i.e. 'this'
     * @param methodToValidate the method to validate, created via {@link #createMethodValidator(Class, String)}
     * @param allMethodParams  all the parameters coming into the method
     * @return a list of constraint violations, or an empty list if no violations were found
     */
    /* package */ static Set<? extends ConstraintViolation<?>> checkParams(
        Object thisMethodObject,
        Method methodToValidate,
        Object... allMethodParams) {

        return methodValidator.validateParameters(thisMethodObject, methodToValidate, allMethodParams);
    }

    /**
     * Perform Jakarta Bean Validation on a method's params, throwing an exception if any violations are found.
     * Package-private — the user will invoke this via {@link MethodValidator#validateParams(Object, Object...)}.
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

        Set<ConstraintViolation<Object>> violations = methodValidator.validateParameters(
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
     * Package-private — the user will invoke this via {@link ConstructorValidator#validateParams(Object...)}.
     *
     * @param constructorToValidate the constructor to validate
     * @param allConstructorParams  all the parameters coming into the constructor
     * @return a list of constraint violations, or an empty list if no violations were found
     */
    /* package */ static Set<? extends ConstraintViolation<?>> checkParams(
        Constructor<?> constructorToValidate,
        Object... allConstructorParams) {

        return methodValidator.validateConstructorParameters(constructorToValidate, allConstructorParams);
    }

    /**
     * Perform Jakarta Bean Validation on a constructor's params, throwing an exception if any violations are found.
     * Package-private — the user will invoke this via {@link ConstructorValidator#validateParams(Object...)}.
     *
     * @param constructorToValidate the constructor whose params we are validating
     * @param constructorParams     the params to validate
     * @throws ConstraintViolationException if any violations are found
     */
    /* package */ static void validateParams(
        Constructor<?> constructorToValidate,
        Object... constructorParams) {

        Set<ConstraintViolation<Object>> violations = methodValidator.validateConstructorParameters(
            constructorToValidate,
            constructorParams);

        if (violations.isEmpty()) {
            return;
        }

        throw new ConstraintViolationException(createViolationString(violations), violations);
    }

    ////////////////////////////////////////
    // METHOD / CONSTRUCTOR VALIDATORS — creation

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

    ////////////////////////////////////////
    // UTILS — other helper methods

    /**
     * Call this inside your {@code MyConstraintValidator.isValid()} method when a constraint is violated.
     * Sets a custom constraint violation message and returns {@code false}.
     */
    public static boolean invalid(ConstraintValidatorContext context, String errorMessage, Object... args) {

        context.disableDefaultConstraintViolation();
        String formattedError = args.length == 0 ? errorMessage : errorMessage.formatted(args);

        context
            .buildConstraintViolationWithTemplate(formattedError)
            .addConstraintViolation();

        return false;
    }

    /**
     * Converts {@link ConstraintViolation}s into friendly error message strings.
     * Useful after calling:
     * <ul>
     *     <li>{@link #check(Object)}</li>
     *     <li>{@link MethodValidator#checkParams(Object, Object...)}</li>
     *     <li>{@link ConstructorValidator#checkParams(Object...)}</li>
     * </ul>
     */
    public static Stream<String> parseViolationMessages(Collection<? extends ConstraintViolation<Object>> violations) {
        return ValidationUtils.parseViolationMessages(violations);
    }

    /**
     * Helper method for creating strongly typed variables from an array of Objects.
     * Useful when creating a method-level (cross-parameter) constraint.
     * Validates that no elements are null, and casts them to the appropriate type.
     *
     * @param objParam an object from the {@code Object[]} array
     * @return strongly-typed version of the parameter
     * @param <T> expected strong type of the parameter
     */
    public static <T> T getObjectParam(Object objParam, Class<T> expectedType) {

        if (objParam == null) {
            throw new IllegalArgumentException("Null parameter.");
        }

        return expectedType.cast(objParam);
    }

    /**
     * Helper method for creating strongly typed variables from an array of Objects.
     * Useful when creating a method-level (cross-parameter) constraint.
     * Validates that no elements are null, and casts them to the appropriate type.
     *
     * @param objParam an object from the {@code Object[]} array
     * @return strongly-typed version of the parameter
     * @param <T> expected strong type of the parameter
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObjectParam(Object objParam) {

        if (objParam == null) {
            throw new IllegalArgumentException("Null parameter.");
        }

        return (T) objParam;
    }
}
